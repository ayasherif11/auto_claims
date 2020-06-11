package com.unilever.autoclaims.security.utils.ldap;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.security.cert.X509Certificate;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

// Socket Factory for Java 8
public class CustomSSLSocketFactory extends SocketFactory {

    private SSLSocketFactory sf;
    public static String sslUrl;

    public CustomSSLSocketFactory() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }

                }
            };

            URL serverUrl = new URL(sslUrl);

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocket sslSocket = (SSLSocket) sc.getSocketFactory().createSocket(serverUrl.getHost(), serverUrl.getPort());
            sslSocket.startHandshake();
            SSLSession sslSession = sslSocket.getSession();
//            for (Certificate certificate : sslSession.getPeerCertificates()) {
//                byte[] encodedCertVal = certificate.getEncoded();
//
//                PemWriter writer = new PemWriter(new FileWriter("testcert.cert"));
//                writer.writeObject(new PemObject("CERTIFICATE", certificate.getEncoded()));
//                writer.flush();
//            }
//            Certificate ca = sslSession.getPeerCertificates()[0];
//
//            String keyStoreType = KeyStore.getDefaultType();
//            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
//            keyStore.load(null, null);
//            keyStore.setCertificateEntry("ca", ca);
//
//            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//            tmf.init(keyStore);
//
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(null, tmf.getTrustManagers(), null);
//            sf = (SSLSocketFactory) sslContext.getSocketFactory();
            sf = (SSLSocketFactory) sc.getSocketFactory();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static synchronized SocketFactory getDefault() {
        final CustomSSLSocketFactory value = new CustomSSLSocketFactory();
        return value;
    }

    public Socket createSocket(final String s, final int i) throws IOException {
        return sf.createSocket(s, i);
    }

    public Socket createSocket(final String s, final int i, final InetAddress inetAddress, final int i1) throws IOException {
        return sf.createSocket(s, i, inetAddress, i1);
    }

    public Socket createSocket(final InetAddress inetAddress, final int i) throws IOException {
        return sf.createSocket(inetAddress, i);
    }

    public Socket createSocket(final InetAddress inetAddress, final int i, final InetAddress inetAddress1, final int i1) throws IOException {
        return sf.createSocket(inetAddress, i, inetAddress1, i1);
    }


}
