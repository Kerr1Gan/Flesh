//package com.ecjtu.flesh;
//
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.Socket;
//import java.security.KeyManagementException;
//import java.security.NoSuchAlgorithmException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLSocket;
//import javax.net.ssl.SSLSocketFactory;
//
///**
// * Created by Ethan_Xiang on 2018/2/22.
// */
//
//public class TLSSocketFactory extends SSLSocketFactory {
//    private SSLSocketFactory delegate;
//
//    public TLSSocketFactory() throws KeyManagementException, NoSuchAlgorithmException {
//        SSLContext context = SSLContext.getInstance("TLS");
//        context.init(null, null, null);
//        delegate = context.getSocketFactory();
//    }
//
//    @Override
//    public String[] getDefaultCipherSuites() {
//        return delegate.getDefaultCipherSuites();
//    }
//
//    @Override
//    public String[] getSupportedCipherSuites() {
//        return delegate.getSupportedCipherSuites();
//    }
//
//    @Override
//    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
//        return enableTLSOnSocket(delegate.createSocket(s, host, port, autoClose));
//    }
//
//    @Override
//    public Socket createSocket(String host, int port) throws IOException {
//        return enableTLSOnSocket(delegate.createSocket(host, port));
//    }
//
//    @Override
//    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
//        return enableTLSOnSocket(delegate.createSocket(host, port, localHost,
//                localPort));
//    }
//
//    @Override
//    public Socket createSocket(InetAddress host, int port) throws IOException {
//        return enableTLSOnSocket(delegate.createSocket(host, port));
//    }
//
//    @Override
//    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
//        return enableTLSOnSocket(delegate.createSocket(address, port, localAddress, localPort));
//    }
//
//    private Socket enableTLSOnSocket(Socket socket) {
//        if (socket != null && (socket instanceof SSLSocket)) {
//
//            String[] protocols = ((SSLSocket) socket).getEnabledProtocols();
//            List<String> supports = new ArrayList<>();
//            if (protocols != null && protocols.length > 0) {
//                supports.addAll(Arrays.asList(protocols));
//            }
//            Collections.addAll(supports, "TLSv1.1", "TLSv1.2");
//            ((SSLSocket) socket).setEnabledProtocols(supports.toArray(new String[supports.size()]));
//        }
//        return socket;
//    }
//}
