
ALTER TABLE INT_SETTINGS
MODIFY (CODE VARCHAR(20), DESCRIPTION VARCHAR(100));

Insert into INT_SETTINGS (CODE,DESCRIPTION, VALUE)
values ('LDAP_URL', 'LDAP URL USED IN S2 ACCOUNT AUTHENTICATION', 'ldaps://cgtsdcs20037.s2.ms.unilever.com:636');

Insert into INT_SETTINGS (CODE,DESCRIPTION,VALUE) 
values ('LDAP_HTTPS_URL', 'LDAP HTTPS URL USED IN S2 ACCOUNT AUTHENTICATION TO GET CERTIFICATE','https://cgtsdcs20037.s2.ms.unilever.com:636');

---------------------------------------------------------------------------------------------------
---------------------------------------------------------------

ALTER TABLE INT_SETTINGS  
MODIFY (CODE NOT NULL);
ALTER TABLE INT_SETTINGS
ADD CONSTRAINT INT_SETTINGS_PK PRIMARY KEY (CODE ) ENABLE;


ALTER TABLE CUMM_DIST  
MODIFY (DISTNO NOT NULL);
ALTER TABLE CUMM_DIST
ADD CONSTRAINT CUMM_DIST_PK PRIMARY KEY (DISTNO ) ENABLE;
---------------------------------------------------------------------------------------------------------------------------------

--------------------  GET DISTRIBUTORS -----------------------------------------------------------------------------------------
SELECT B.BRANCHNO, B.BRANCHNAMEE, B.SAP_CODE, B.REGION, B.TYPE , DIST.SEL
    
FROM SAS_TUNISIA.BRANCH B

JOIN CUMM_DIST DIST ON DIST.DISTNO = B.BRANCHNO;

/
create or replace PROCEDURE GET_SAS2_DISTRIBUTORS
AS  
BEGIN
	DELETE cumm_dist;
	
	insert into cumm_dist(distno , sch)
	( select BRANCHNO ,SCHEMANAMEE
	  from SAS_TUNISIA.branch 
	  where Active = 'Y'
	  AND SASII = 'Y');
END GET_SAS2_DISTRIBUTORS;
/
------------------------------------------------------------------------------------------------------------------------

--------------------  GET DATA -----------------------------------------------------------------------------------------
ALTER TABLE U2K2_CLAIMS_DATA
ADD CONSTRAINT U2K2_CLAIMS_DATA_PK PRIMARY KEY ( CONDITIONTYPE,
                                            ITEMNO,
                                            DISCOUNTTYPE,
                                            DISTNO,
                                            PERIOD,
                                            INTERNAL_ORDER);
/

create or replace PROCEDURE DUMP_GENERATOR(P_start_date	VARCHAR2,P_end_date	VARCHAR2,P_DISTNO varchar2,P_PERIOD	VARCHAR2) IS
Begin
	 	DELETE	U2K2_CLAIMS_DATA
	 	WHERE	  DISTNO = P_DISTNO
    AND     (
             TO_DATE(PERIOD, 'DD/MM/YYYY') = TO_DATE(P_PERIOD, 'DD/MM/YYYY')
             OR
             TO_DATE(PERIOD, 'DD/MM/YYYY') < ADD_MONTHS(TRUNC(SYSDATE, 'MM'), -1)
            );

--message_box('dump_generator step 1');

    DELETE FROM T$U2K2_CLAIMS_DATA_ORDERS;

--message_box('dump_generator step 2');

    INSERT INTO T$U2K2_CLAIMS_DATA_ORDERS
    (CONDITIONTYPE, ITEMNO, DISCOUNT, DISCOUNTTYPE, INTERNAL_ORDER, ORDERNO)
    (
					SELECT	/*+ index(s s_scenario_pk) */ 
							CT.CONDITION_TYPE,OD.ITEMNO,(Decode(O.OrderType,1,1,-1)*OD.GF_Value)DISCOUNT,
							'Promotion Discount' DISCOUNTTYPE, P.INTERNAL_ORDER, O.ORDERNO
			 		from		Orders O, OrderProm_Det OD, CONDITION_TYPE CT, SAS_TUNISIA.S_PROMOTION P
			 		Where		O.OrderNo = OD.OrderNo
			 		AND     O.BRANCHNO = OD.BRANCHNO
			 		AND		  OD.PROM_ID=P.PROM_ID
			 		--AND			CT.SC_TYPEID=OD.SC_TYPEID
			 		AND     CT.SC_TYPEID = NVL(P.F1, OD.SC_TYPEID)
			 		and			O.OrderStatus =3
			 		and			O.DeliveryDate between TO_DATE(P_Start_Date,'DD/MM/YYYY') and TO_DATE(P_END_Date,'DD/MM/YYYY')
			 		and			OD.LineType in ('NLI', 'DSC', 'PSC')
			 		and			OD.Free_ItemNo is Null
			 		AND     NVL(O.AUTOCLAIMS_USED, 'N') <> 'Y'
			 		UNION ALL
			 		select 		CT.CONDITION_TYPE,OD.ITEMNO,(Decode(O.OrderType,1,1,-1)*od.cashdiscount)DISCOUNT,
										'Cash Discount'DISCOUNTTYPE, cs.value INTERNAL_ORDER, O.ORDERNO
			 		from		Orders O, ORDERDETAILS OD, CONDITION_TYPE CT, cumm_settings cs
			 		Where		CS.CODE = 'CASH_IO'
			 		AND     O.OrderNo = OD.OrderNo
			 		AND     O.BRANCHNO = OD.BRANCHNO
			 		and			O.OrderStatus =3
			 		and			O.DeliveryDate between TO_DATE(P_Start_Date,'DD/MM/YYYY') and TO_DATE(P_END_Date,'DD/MM/YYYY')
			 		and			OD.LineType in ( 'NLI','DSC')
			 		and			CT.SC_TYPEID = '99'
			 		AND			O.OVERPERIOD='N'--ON INVOICE
			 		AND			OD.CASHDISCOUNT <> 0
			 		AND     NVL(O.AUTOCLAIMS_USED, 'N') <> 'Y'
					UNION ALL
			 		select 		CT.CONDITION_TYPE,OD.ITEMNO,(Decode(O.OrderType,1,1,-1)*(OD.SUBTOTAL-OD.SALESTAX)*S.VALUE/100)DISCOUNT,
										'RMD Discount'DISCOUNTTYPE, cs.value INTERNAL_ORDER, O.ORDERNO
			 		from		Orders O, ORDERDETAILS OD, CONDITION_TYPE CT,CUMM_SETTINGS S, cumm_settings cs
			 		Where		CS.CODE = '5RMD_IO'
			 		and     O.OrderNo = OD.OrderNo
			 		AND     O.BRANCHNO = OD.BRANCHNO
			 		and			O.OrderStatus =3
			 		and			O.DeliveryDate between TO_DATE(P_Start_Date,'DD/MM/YYYY') and TO_DATE(P_END_Date,'DD/MM/YYYY')
			 		and			OD.LineType in ( 'NLI','DSC')
			 		and			CT.SC_TYPEID = 'RMD'
			 		AND			s.code='RMD_PER'
			 		AND			(OD.SUBTOTAL-OD.SALESTAX) <> 0
			 		AND     NVL(O.AUTOCLAIMS_USED, 'N') <> 'Y'
    );

--message_box('dump_generator step 3');

    UPDATE SAS_TUNISIA.ORDERS O
    SET    O.AUTOCLAIMS_USED = 'I'
    WHERE  O.ORDERNO IN (SELECT T.ORDERNO FROM T$U2K2_CLAIMS_DATA_ORDERS T)
    AND    O.BRANCHNO = P_DISTNO;

--message_box('dump_generator step 4');

	 	INSERT INTO U2K2_CLAIMS_DATA (
		     CONDITIONTYPE, ITEMNO, DISCOUNT, 
   			 DISCOUNTTYPE, DISTNO, PERIOD, 
   			INTERNAL_ORDER)
		(
			SELECT
					CONDITIONTYPE,ITEMNO,SUM(DISCOUNT)DISCOUNT,
					DISCOUNTTYPE,p_distno,p_period, INTERNAL_ORDER
      FROM   T$U2K2_CLAIMS_DATA_ORDERS
			GROUP BY  CONDITIONTYPE,ITEMNO,
					DISCOUNTTYPE,p_distno,p_period, INTERNAL_ORDER
			HAVING SUM(DISCOUNT) <> 0
			);

--message_box('dump_generator step 5');

    COMMIT;
END;
/
create or replace FUNCTION GENERATE_U2K2_CLAIMS_DATA( PERIOD     DATE,
												 USER_ID     VARCHAR2)
  RETURN VARCHAR2
IS 
	vStart_Date	  Date;
	vEnd_Date		  Date;
	no_synon	    NUMBER;
    vPeriod       VARCHAR2(30);
BEGIN

  vPeriod:=to_char(PERIOD,'dd/mm/yyyy');

	SELECT trunc(period,'MM')
	INTO vStart_date
	FROM DUAL;

	vEnd_date:= period;


	FOR distloop IN (   Select SCH SCH,DISTNO BRANCHNO
						from CUMM_dist
						where sel = 'Y'
						ORDER BY 1)
	LOOP
			delete from claims_ctrl
			where distno = distloop.branchno 
			and period = vPeriod;

			delete from u2k2_claims
			where distno = distloop.branchno 
			and PERIOD = vPeriod;

			delete from u2k2_claims_data
			where distno = distloop.branchno 
			and PERIOD = vPeriod;


			INSERT INTO claims_ctrl(DISTNO, PERIOD, CLAIM_TYPE, CONFIRMED, LOADED, USR_GET_DATA, USR_CONFIRMED)
			values( distloop.branchno, vPeriod, 'A', 'N', 'N', USER_ID, Null);
			no_synon := 0;
			
			begin
				EXECUTE IMMEDIATE 'DROP VIEW ORDERS';
				Exception When Others then null;
			end;
				

			begin
				EXECUTE IMMEDIATE 'CREATE VIEW ORDERS AS SELECT * FROM SAS_TUNISIA.ORDERS WHERE BRANCHNO = ''' || distloop.BRANCHNO || '''';
				Exception When Others then no_synon := 1;
			end;
				
		    begin
			    EXECUTE IMMEDIATE 'DROP VIEW ORDERDETAILS';
				Exception When Others then null;
			end;
				
			begin
				EXECUTE IMMEDIATE 'CREATE VIEW ORDERDETAILS AS SELECT * FROM SAS_TUNISIA.ORDERDETAILS WHERE BRANCHNO = ''' || distloop.BRANCHNO ||'''';
				Exception When Others
                  then no_synon := 1;
			end;
				

		    begin
				EXECUTE IMMEDIATE 'DROP VIEW ORDERPROM_MSTR';
				Exception When Others
                  then null;
			end;
			
			begin
				EXECUTE IMMEDIATE 'CREATE VIEW ORDERPROM_MSTR AS SELECT * FROM SAS_TUNISIA.ORDERPROM_MSTR WHERE BRANCHNO = ''' || distloop.BRANCHNO || '''';
				Exception When Others
                  then no_synon := 1;
			end;
			

			begin
				EXECUTE IMMEDIATE 'DROP VIEW ORDERPROM_DET';
				Exception When Others 
                  then null;
			end;


			begin
				EXECUTE IMMEDIATE'CREATE VIEW ORDERPROM_DET AS 
				SELECT * FROM SAS_TUNISIA.ORDERPROM_DET WHERE BRANCHNO = ''' || distloop.BRANCHNO || '''';
				Exception When Others 
                  then no_synon :=1;
			end;

			If (no_synon =1) then
				RETURN 'Probl?me en cr?ant des synonymes pour '||distloop.SCH ||'. Svp essai encore';
			end if;


			DUMP_GENERATOR(TO_CHAR(vStart_Date,'DD/MM/YYYY'), TO_CHAR(vEnd_Date,'DD/MM/YYYY'), distloop.BRANCHNO, vPeriod);
			
			COMMIT;
      RETURN 'SUCCESS';
      
			
	END LOOP;
END GENERATE_U2K2_CLAIMS_DATA;
/
------------------------------------------------------------------------------------------------------------------------


--------------------  LOAD DATA -----------------------------------------------------------------------------------------
create or replace FUNCTION LOAD_U2K2_CLAIMS_DATA(PERIOD DATE) RETURN VARCHAR2

IS 

	vConfirm varchar2(1);
	vLoad varchar2(1);
	done number := 0;
  vResult varchar2(1000);
  vPeriod       VARCHAR2(30);
  
begin

 vPeriod:=to_char(PERIOD,'dd/mm/yyyy');
 
 
	--KEEP HISTORY OF THE CURRENTLY
	INSERT INTO u2k2_claims_data_HIST(CONDITIONTYPE, discount, DISCOUNTTYPE, DISTNO, PERIOD, INTERNAL_ORDER ,DATETIMESTAMP)
	 (SELECT CONDITIONTYPE, discount, DISCOUNTTYPE, DISTNO, vPeriod, INTERNAL_ORDER ,SYSDATE 
	 FROM u2k2_claims_data 
	 WHERE 	period= vPeriod
	 AND	distno IN (SELECT distno FROM int_dist));
	
		for i in (  select 	distno 
                from CUMM_dist
                WHERE SEL = 'Y'
							--where 	distno = :cumm_dist.distno -- added by farag at 20-10-2008
							)
		loop
		Begin
				select nvl(loaded,'N'), nvl(confirmed, 'N')
				into vLoad, vConfirm
				from claims_ctrl
				where period = vPeriod
				and distno = i.distno;
				
			if vLoad = 'N' and vConfirm = 'Y' then			
				       insert into U2K2_claims (CONDITIONTYPE, ITEMNO, AMT, RMKS, DISTNO, PERIOD, INTERNAL_ORDER ) 
				      						(select CONDITIONTYPE, ITEMNO, discount, DISCOUNTTYPE, DISTNO, vPeriod, INTERNAL_ORDER 
				      						 from u2k2_claims_data, SAS_TUNISIA.branch b
											 where 	b.BRANCHNO = i.DISTNO
											 and  	period = vPeriod
											 and 	distno = i.distno
											 AND 	discount <> 0 
				      					   );
				      	update claims_ctrl
								set loaded = 'Y'
								where period = vPeriod
								and distno = i.distno;

							PROC_INSERT_ECC_CREDIT_NOTE(I.DISTNO);

					    UPDATE SAS_TUNISIA.ORDERS O
					    SET    O.AUTOCLAIMS_USED = 'Y',
					           O.AUTOCLAIMS_DATE = SYSDATE
					    WHERE  O.BRANCHNO = I.DISTNO
					    AND    NVL(O.AUTOCLAIMS_USED, 'N') = 'I';
								
				 			done :=  1;
			ELSif vLoad <> 'N' then
						vResult := 'LOAD_ERROR--x--1--x--1--x--' || i.distno;
						--'Vous ne pouvez pas charger des donn?es de distributeur:'||i.distno||'. Il est d?j? charg? avant.'
			ELSif vConfirm <> 'Y' then
						 vResult := 'CONFIRM_ERROR--x--1--x--1--x--' || i.distno;
						 --'Vous ne pouvez pas charger des donn?es de distributeur:'||i.distno||'. Il il n est pas confirm?'
			end if;
    exception
          when no_data_found then
          null;
    end;
end loop;
		
	if done = 1 then
			commit;
			 vResult := 'success';
			--'Donn?es ont ?t? charg?es pour tout distributeurs en selection'		
	end if;
   RETURN vResult;
end LOAD_U2K2_CLAIMS_DATA;
/
------------------------------------------------------------------------------------------------------------------------


--------------------  CONFIRM DATA -----------------------------------------------------------------------------------------
create or replace FUNCTION CONFIRM_U2K2_CLAIMS_DATA(CLAIM_TYPE VARCHAR2,
											   PERIOD DATE) RETURN VARCHAR2 IS


vPeriod       VARCHAR2(30);
BEGIN

  vPeriod:=to_char(PERIOD,'dd/mm/yyyy');


	for i in (select 	distno 
						from 		cumm_dist 
						where			SEL = 'Y')
		loop
			update claims_ctrl
				set (confirmed, usr_confirmed) =
						(select  'Y', OSUSER
						FROM    sys.v_$session
						WHERE AUDSID = USERENV('SESSIONID'))
			where distno = i.distno
			and period = vPeriod
			and claim_type = CLAIM_TYPE;
      
    end loop;
    
		commit;
		
	RETURN 'SUCCESS';
EXCEPTION 
        WHEN NO_DATA_FOUND THEN
        ROLLBACK;
        RETURN NULL;

END CONFIRM_U2K2_CLAIMS_DATA;
------------------------------------------------------------------------------------------------------------------------


--------------------  REPORT DATA -----------------------------------------------------------------------------------------
CREATE VIEW V$U2K2_CLAIMS_DATA
AS SELECT DISTNO, B.BRANCHNAMEE, CONDITIONTYPE,ITEMNO,DISCOUNT,DISCOUNTTYPE,PERIOD,INTERNAL_ORDER
FROM SAS_TUNISIA.BRANCH B
JOIN U2K2_CLAIMS_DATA U ON u.DISTNO = B.BRANCHNO
order by to_number(branchno),itemno;
/