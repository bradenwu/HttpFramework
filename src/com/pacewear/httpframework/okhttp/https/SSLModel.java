
package com.pacewear.httpframework.okhttp.https;

import com.pacewear.httpframework.common.InputStreamUtil;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.KeyStore.LoadStoreParameter;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SSLModel {
    public SSLModel() {
    }

    public static SSLModel.SSLParams getSslSocketFactory(SSLParcelParam param) {
        String[] strCertFicates = param.getCertInput();
        String bksFile = param.getBksFile();
        String pwd = param.getPwd();
        if (strCertFicates == null || strCertFicates.length <= 0) {
            return null;
        }
        InputStream[] certificates = new InputStream[strCertFicates.length];
        int cnt = 0;
        for (String tmp : strCertFicates) {
            if (cnt >= strCertFicates.length) {
                break;
            }
            certificates[cnt] = InputStreamUtil.string2InputStream(tmp);
            cnt++;
        }
        InputStream bksFileInput = InputStreamUtil.string2InputStream(bksFile);
        return getSslSocketFactory(certificates, bksFileInput, pwd);
    }

    public static SSLModel.SSLParams getSslSocketFactory(InputStream[] certificates,
            InputStream bksFile, String password) {
        SSLModel.SSLParams sslParams = new SSLModel.SSLParams();

        try {
            TrustManager[] trustManagers = prepareTrustManager(certificates);
            KeyManager[] keyManagers = prepareKeyManager(bksFile, password);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            X509TrustManager trustManager = null;
            if (trustManagers != null) {
                trustManager = new SSLModel.MyTrustManager(chooseTrustManager(trustManagers));
            } else {
                trustManager = new SSLModel.UnSafeTrustManager();
            }

            sslContext.init(keyManagers, new TrustManager[] {
                    (TrustManager) trustManager
            }, (SecureRandom) null);
            sslParams.sSLSocketFactory = sslContext.getSocketFactory();
            sslParams.trustManager = (X509TrustManager) trustManager;
            return sslParams;
        } catch (NoSuchAlgorithmException var8) {
            throw new AssertionError(var8);
        } catch (KeyManagementException var9) {
            throw new AssertionError(var9);
        } catch (KeyStoreException var10) {
            throw new AssertionError(var10);
        }
    }

    private static TrustManager[] prepareTrustManager(InputStream... certificates) {
        if (certificates != null && certificates.length > 0) {
            try {
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load((LoadStoreParameter) null);
                int index = 0;
                InputStream[] trustManagerInput = certificates;
                int len$ = certificates.length;

                for (int i$ = 0; i$ < len$; ++i$) {
                    InputStream certificate = trustManagerInput[i$];
                    String certificateAlias = Integer.toString(index++);
                    keyStore.setCertificateEntry(certificateAlias,
                            certificateFactory.generateCertificate(certificate));

                    try {
                        if (certificate != null) {
                            certificate.close();
                        }
                    } catch (IOException var10) {
                        ;
                    }
                }

                trustManagerInput = null;
                TrustManagerFactory trustManagerFactory = TrustManagerFactory
                        .getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                return trustManagers;
            } catch (NoSuchAlgorithmException var11) {
                var11.printStackTrace();
            } catch (CertificateException var12) {
                var12.printStackTrace();
            } catch (KeyStoreException var13) {
                var13.printStackTrace();
            } catch (Exception var14) {
                var14.printStackTrace();
            }

            return null;
        } else {
            return null;
        }
    }

    private static KeyManager[] prepareKeyManager(InputStream bksFile, String password) {
        try {
            if (bksFile != null && password != null) {
                KeyStore clientKeyStore = KeyStore.getInstance("BKS");
                clientKeyStore.load(bksFile, password.toCharArray());
                KeyManagerFactory keyManagerFactory = KeyManagerFactory
                        .getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManagerFactory.init(clientKeyStore, password.toCharArray());
                return keyManagerFactory.getKeyManagers();
            }

            return null;
        } catch (KeyStoreException var4) {
            var4.printStackTrace();
        } catch (NoSuchAlgorithmException var5) {
            var5.printStackTrace();
        } catch (UnrecoverableKeyException var6) {
            var6.printStackTrace();
        } catch (CertificateException var7) {
            var7.printStackTrace();
        } catch (IOException var8) {
            var8.printStackTrace();
        } catch (Exception var9) {
            var9.printStackTrace();
        }

        return null;
    }

    private static X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        TrustManager[] arr$ = trustManagers;
        int len$ = trustManagers.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            TrustManager trustManager = arr$[i$];
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }

        return null;
    }

    private static class MyTrustManager implements X509TrustManager {
        private X509TrustManager defaultTrustManager;
        private X509TrustManager localTrustManager;

        public MyTrustManager(X509TrustManager localTrustManager)
                throws NoSuchAlgorithmException, KeyStoreException {
            TrustManagerFactory var4 = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            var4.init((KeyStore) null);
            this.defaultTrustManager = SSLModel.chooseTrustManager(var4.getTrustManagers());
            this.localTrustManager = localTrustManager;
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            try {
                this.defaultTrustManager.checkServerTrusted(chain, authType);
            } catch (CertificateException var4) {
                this.localTrustManager.checkServerTrusted(chain, authType);
            }

        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class UnSafeTrustManager implements X509TrustManager {
        private UnSafeTrustManager() {
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private class UnSafeHostnameVerifier implements HostnameVerifier {
        private UnSafeHostnameVerifier() {
        }

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    public static class SSLParams {
        public SSLSocketFactory sSLSocketFactory;
        public X509TrustManager trustManager;

        public SSLParams() {
        }
    }
}
