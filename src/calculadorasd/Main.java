package calculadorasd;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 *
 * @author david
 */
public class Main {

    public static void main(String args[]) throws IOException
    {
        /**
         * filesystem define a pasta local aonde os arquivos estáticos serão servidos.
         * a url acessada deverá ser http://localhost:8080/calculadora/, assim:
         * urlprefix terá o prefixo /calculadora.
         * o arquivo default que será acessado caso o usuário não coloque .html ou .htm
         * para ser acessado será chamado index.html por directoryIndex.
        */
        String urlprefix = "/calculadora/";
        String filesystem = "html/";
        String directoryIndex = "index.html";
        
        //Configurando o handler para web responder as requisições estáticas.
        //StaticFileHandler sfh = new StaticFileHandler(urlprefix, filesystem, directoryIndex);
        HttpServer server; //Servidor web.
        server = HttpServer.create(new InetSocketAddress("::", 8080), 0); //Definindo as configurações do servidor web
        
        //Iniciando o servidor para o handler e as configurações de prefixo, local e arquivo default index.html.
        StaticFileHandler.create(server, urlprefix, filesystem, directoryIndex);
        
        System.out.println("Servidor pronto...");
        //Iniciando o servidor.
        server.start();
             
    }
    
}
