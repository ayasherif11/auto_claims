
DELETE FROM INT_SETTINGS WHERE CODE = 'LDAP_URL';
DELETE FROM INT_SETTINGS WHERE CODE = 'LDAP_HTTPS_URL';

ALTER TABLE INT_SETTINGS
MODIFY (CODE VARCHAR(10), DESCRIPTION VARCHAR(15));

---------------------------------------------------------- 
------------------------------------------------------

ALTER TABLE INT_SETTINGS
DROP CONSTRAINT INT_SETTINGS_PK;

ALTER TABLE INT_SETTINGS MODIFY (CODE NULL);

ALTER TABLE CUMM_DIST
DROP CONSTRAINT CUMM_DIST_PK;

ALTER TABLE CUMM_DIST MODIFY (DISTNO NULL);

------------------------------------------------------------


DROP PROCEDURE GET_SAS2_DISTRIBUTORS;
DROP VIEW V$CUMM_DIST_GRID;
-------------------------------------------------------------


DROP FUNCTION GENERATE_U2K2_CLAIMS_DATA;
DROP PROCEDURE DUMP_GENERATOR;
------------------------------------------------------------


ALTER TABLE U2K2_CLAIMS_DATA                                         
DROP CONSTRAINT U2K2_CLAIMS_PK;
---------------------------------------------------------------

DROP FUNCTION CONFIRM_U2K2_CLAIMS_DATA;
---------------------------------------------------------------

DROP FUNCTION LOAD_U2K2_CLAIMS_DATA;
---------------------------------------------------------------

DROP VIEW V$U2K2_CLAIMS_DATA;
----------------------------------------------------------------