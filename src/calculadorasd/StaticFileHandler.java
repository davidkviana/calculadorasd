
/**
 * Esta classe é responsável por receber as requisições do site e retornar
 * a aplicação da calculadora. Também recebe a expressão da calculadora e
 * pré-processa a expressão para ser transformada na expressão que o 
 * servidor PHP irá responder.
 */
package calculadorasd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class StaticFileHandler implements HttpHandler {
    /**
     * Mapeamento dos tipos de retorno que o HTTP deverá fazer para cada tipo 
     * de arquivo ou objeto solicitado no site.
     */
    private static final Map<String,String> MIME_MAP = new HashMap<>();
    static {
        MIME_MAP.put("appcache", "text/cache-manifest");
        MIME_MAP.put("css", "text/css");
        MIME_MAP.put("gif", "image/gif");
        MIME_MAP.put("html", "text/html");
        MIME_MAP.put("js", "application/javascript");
        MIME_MAP.put("json", "application/json");
        MIME_MAP.put("jpg", "image/jpeg");
        MIME_MAP.put("jpeg", "image/jpeg");
        MIME_MAP.put("mp4", "video/mp4");
        MIME_MAP.put("pdf", "application/pdf");
        MIME_MAP.put("png", "image/png");
        MIME_MAP.put("svg", "image/svg+xml");
        MIME_MAP.put("xlsm", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        MIME_MAP.put("xml", "application/xml");
        MIME_MAP.put("zip", "application/zip");
        MIME_MAP.put("md", "text/plain");
        MIME_MAP.put("txt", "text/plain");
        MIME_MAP.put("php", "text/plain");
    };
    
    /**
         * filesystemRoot define a pasta local aonde os arquivos estáticos serão servidos.
         * a url acessada deverá ser http://localhost:8080/calculadora/, assim:
         * urlPrefix terá o prefixo /calculadora.
         * o arquivo default que será acessado caso o usuário não coloque .html ou .htm
         * para ser acessado será chamado index.html por directoryIndex.
        */
    private String filesystemRoot;
    private String urlPrefix;
    private String directoryIndex;

    /**
     * @param urlPrefix Prefixo de todas as urls.
     * @param filesystemRoot Pasta raiz dos arquivos estáticos. Apenas arquivos nessa pasta serão servidos.
     * @param directoryIndex aruqivos padrão apresentado na url 'raiz' "index.html".
     */
    public StaticFileHandler(String urlPrefix, String filesystemRoot, String directoryIndex) {
        if (!urlPrefix.startsWith("/")) {
            throw new RuntimeException("path não começa com /");
        }
        if (!urlPrefix.endsWith("/")) {
            throw new RuntimeException("path não termina com /");
        }
        this.urlPrefix = urlPrefix;

        assert filesystemRoot.endsWith("/");
        try {
            this.filesystemRoot = new File(filesystemRoot).getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.directoryIndex = directoryIndex;
    }

    /**
     * Cria e registra um novo handler estático.
     * @param hs Servidor HTTP onde os arquivos serão servidos.
     * @param path Caminho prefixado aonde as requisições terão por padrão esse nome no início.
     * @param filesystemRoot Local dos arquivos no sistema.
     * @param directoryIndex Arquivo para mostrar quando um diretório é requisitado; por exemplo "index.html".
     */
    public static void create(HttpServer hs, String path, String filesystemRoot, String directoryIndex) {
        StaticFileHandler sfh = new StaticFileHandler(path, filesystemRoot, directoryIndex);
        hs.createContext(path, sfh);
    }

    /**
     * Método handle que atenderá as requisições e servirá os arquivos.
    */
    public void handle(HttpExchange he) throws IOException {
        
        String method = he.getRequestMethod(); //Recebe o método da requisição enviada;
        
        String cmd =  he.getRequestURI().toString(); //Recebe o caminho completo da requisição enviada;
        
        //Esta aplicação só recebe esses tipos de requisção.
        if (!("HEAD".equals(method) || "GET".equals(method) || "POST".equals(method))) {
            sendError(he, 501, "Unsupported HTTP method");
            return;
        }
        
        //Se foi enviado um post
        if ("POST".equals(method))
        {
            //Caso o post tenha a requisição para 'calculo?' a epxressão será pré-processada
            if (cmd.contains("calculo?"))
            {
                he.getResponseHeaders().set("Content-Type", "text/plain");
                cmd = cmd.split("\\=")[1];
                
                //Pré ajuste da expressão por conta do envio em javascript.
                System.out.println("Cmd1: "+cmd); //Como a expressão chegou
                cmd = cmd.replace("+", " + ");
                cmd = cmd.replace("-", " - ");
                cmd = cmd.replace("*", " * ");
                cmd = cmd.replace("/", " / ");
                cmd = cmd.replace("(", " ( ");   
                cmd = cmd.replace(")", " ) ");   
                cmd = cmd.replace("%20", "");    
                
                System.out.println("Cmd2: "+cmd);//Como a expressão será processada para ser convertida de forma correta para o PHP
                
                String infix = cmd;
                System.out.printf("infix:   %s%n", infix);//A expressão entregue
                System.out.printf("postfix: %s%n", UndoRPN.infixToPostfix(infix)); //A expressão em formato de árvore
                
                //Este método realiza a chamada de cada operação ao servidor PHP.
                //Aqui neste ponto que as requisições são feitas para o cálculo de cada operação no servidor.
                RPN.evalRPN(UndoRPN.infixToPostfix(infix)); //Após o processamento o recultado é colocado em RPN.result.
                //System.out.printf("Operações:   %s%n", RPN.operations);

                he.sendResponseHeaders(200, RPN.result.length()); //Criando o cabeçalho de resposta à requisição do cálculo.
                OutputStream os = he.getResponseBody(); //Objeto de respota da requisição.
                String s = RPN.result; //Reultado da expressão.
                os.write(s.getBytes());  //Escrevendo o resultado para mostrar para a calculadora web.
                os.flush();  //Confirmando a escrita no outputstream.
                os.close(); //Fechando o outputstream.
                System.out.println("Terminou");
            }
        }
        
        //Daqui em diante o handler é usado apenas para acessar os arquivos estáticos.
        String wholeUrlPath = he.getRequestURI().getPath();
        //System.out.println("wholeUrlPath: "+wholeUrlPath);
        if (wholeUrlPath.endsWith("/")) {
            wholeUrlPath += directoryIndex;
        }
        if (! wholeUrlPath.startsWith(urlPrefix)) {
            throw new RuntimeException("Path is not in prefix - incorrect routing?");
        }
        String urlPath = wholeUrlPath.substring(urlPrefix.length());
        //System.out.println("URLPATH: "+urlPath);
        File f = new File(filesystemRoot, urlPath);
        File canonicalFile;
        try {
            canonicalFile = f.getCanonicalFile();
            //System.out.println("Aqui0: "+urlPath);
            
        } catch (IOException e) {
            // This may be more benign (i.e. not an attack, just a 403),
            // but we don't want the attacker to be able to discern the difference.
            reportPathTraversal(he);
            //System.out.println("Aqui1: "+urlPath);
            return;
        }

        String canonicalPath = canonicalFile.getPath();
        if (! canonicalPath.startsWith(filesystemRoot)) {
            reportPathTraversal(he);
            //System.out.println("Aqui2: "+urlPath);
            return;
        }

        FileInputStream fis;
        String mimeType;
        try {
            fis = new FileInputStream(canonicalFile);
            //System.out.println("Aqui4: "+urlPath);
            mimeType = lookupMime(urlPath);
            
        } catch (FileNotFoundException e) {
            // The file may also be forbidden to us instead of missing, but we're leaking less information this way 
            sendError(he, 404, "File not found");
            return;
        }

        
        he.getResponseHeaders().set("Content-Type", mimeType);
        //System.out.println("CHEGOU AQUI: "+urlPath);
        
        if ("POST".equals(method)) {
            //System.out.println("POST: "+urlPath);
            
            he.sendResponseHeaders(200, canonicalFile.length());            
            OutputStream os = he.getResponseBody();
            copyStream(fis, os);
            os.close();
            if (cmd.contains("calculo?"))
            {
                cmd = cmd.split("\\=")[1];
                System.out.println("Cmd2: "+cmd);
            }
        }
        else if ("GET".equals(method)) {
            he.sendResponseHeaders(200, canonicalFile.length());
            
            OutputStream os = he.getResponseBody();
            System.out.println(os.toString());
            copyStream(fis, os);
            os.close();
        } else {
            assert("HEAD".equals(method));
            he.sendResponseHeaders(200, -1);
        }
        fis.close();
    }

    private void copyStream(InputStream is, OutputStream os) throws IOException {
        byte[] buf = new byte[4096];
        int n;
        while ((n = is.read(buf)) >= 0) {
            os.write(buf, 0, n);
        }
    }

    private void sendError(HttpExchange he, int rCode, String description) throws IOException {
        String message = "HTTP error " + rCode + ": " + description;
        byte[] messageBytes = message.getBytes("UTF-8");

        he.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        he.sendResponseHeaders(rCode, messageBytes.length);
        OutputStream os = he.getResponseBody();
        os.write(messageBytes);
        os.close();
    }

    // This is one function to avoid giving away where we failed 
    private void reportPathTraversal(HttpExchange he) throws IOException {
        sendError(he, 400, "Path traversal attempt detected");
    }

    private static String getExt(String path) {
        int slashIndex = path.lastIndexOf('/');
        String basename = (slashIndex < 0) ? path : path.substring(slashIndex + 1);

        int dotIndex = basename.lastIndexOf('.');
        if (dotIndex >= 0) {
            return basename.substring(dotIndex + 1);
        } else {
            return "";
        }
    }

    private static String lookupMime(String path) {
        String ext = getExt(path).toLowerCase();
        return MIME_MAP.getOrDefault(ext, "application/octet-stream");
    }
}
