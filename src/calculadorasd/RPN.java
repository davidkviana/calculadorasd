package calculadorasd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.LinkedList;
import javax.net.ssl.HttpsURLConnection;
/**
 * Classe que faz a separação e execução da expressão em formato de árvore.
 * Assim, caso a expressão seja bc*+a será separado e executado incialmente b * c:
     +
    / \
   *   a
  / \
 b   c
 Assim, b * c será executado e resultando em:
    +
   / \
  r1  a
 Por fim r1 + a será executado em seguida.
 */
public class RPN{
    //operations recebe a operação que deverá ocorrer;
    public static String operations = "";
    //result recebe o resultado final após todas as operações;
    public static String result = "";
    
    /**
     * evalRPN recebe uma expressão, quebra em várias e transforma cada uma no formato:
     * oper1=val1&oper2=val2&operacao=tipo 
     * em seguida chama o servidor PHP que processa a operação;
     * @param expr é uma string no formato bc*+a;
     */
	public static void evalRPN(String expr){
            
		String cleanExpr = cleanExpr(expr);
		LinkedList<Double> stack = new LinkedList<Double>();
		System.out.println("Input\tOperation\tStack after");
		for(String token:cleanExpr.split("\\s")){
			System.out.print(token+"\t");
			Double tokenNum = null;
			try{
				tokenNum = Double.parseDouble(token);
			}catch(NumberFormatException e){}
			if(tokenNum != null){
				System.out.print("Push\t\t");
				stack.push(Double.parseDouble(token+""));
			}else if(token.equals("*")){
				System.out.print("Operate *\t\t");
				double secondOperand = stack.pop();
				double firstOperand = stack.pop();
				//stack.push(firstOperand * secondOperand);
                                //Neste ponto a expressão tem os 3 parametros e é montada a expressão para
                                //chamar o servidor PHP.
                                RPN.operations = RPN.operations+"oper1="+firstOperand+"&oper2="+secondOperand+"&operacao=3\r\n";
                                //Aqui é feita a requisição do cliente com a operação no formato:
                                //oper1=val1&oper2=val2&operacao=tipo
                                //res recebe o resultado
                                String res = RPN.calculadorClientHTTP(RPN.operations);
                                //o resultado é armazenado pois poderá ser usado na próxima operação.
                                stack.push(Double.parseDouble(res));
                                RPN.operations = "";
			}else if(token.equals("/")){
				System.out.print("Operate /\t\t");
				double secondOperand = stack.pop();
				double firstOperand = stack.pop();
				//stack.push(firstOperand / secondOperand);
                                //Neste ponto a expressão tem os 3 parametros e é montada a expressão para
                                //chamar o servidor PHP.
                                RPN.operations = RPN.operations+"oper1="+firstOperand+"&oper2="+secondOperand+"&operacao=4\r\n";
                                //Aqui é feita a requisição do cliente com a operação no formato:
                                //oper1=val1&oper2=val2&operacao=tipo
                                //res recebe o resultado
                                String res = RPN.calculadorClientHTTP(RPN.operations);
                                //o resultado é armazenado pois poderá ser usado na próxima operação.
                                stack.push(Double.parseDouble(res));
                                RPN.operations = "";
			}else if(token.equals("-")){
				System.out.print("Operate -\t\t");
				double secondOperand = stack.pop();
				double firstOperand = stack.pop();
				//stack.push(firstOperand - secondOperand);
                                //Neste ponto a expressão tem os 3 parametros e é montada a expressão para
                                //chamar o servidor PHP.
                                RPN.operations = RPN.operations+"oper1="+firstOperand+"&oper2="+secondOperand+"&operacao=2\r\n";
                                //Aqui é feita a requisição do cliente com a operação no formato:
                                //oper1=val1&oper2=val2&operacao=tipo
                                //res recebe o resultado
                                String res = RPN.calculadorClientHTTP(RPN.operations);
                                //o resultado é armazenado pois poderá ser usado na próxima operação.                 
                                stack.push(Double.parseDouble(res));
                                RPN.operations = "";
			}else if(token.equals("+")){
				System.out.print("Operate +\t\t");
				double secondOperand = stack.pop();
				double firstOperand = stack.pop();
				//stack.push(firstOperand + secondOperand);
                                //Neste ponto a expressão tem os 3 parametros e é montada a expressão para
                                //chamar o servidor PHP.
                                RPN.operations = RPN.operations+"oper1="+firstOperand+"&oper2="+secondOperand+"&operacao=1\r\n";
                                //Aqui é feita a requisição do cliente com a operação no formato:
                                //oper1=val1&oper2=val2&operacao=tipo
                                //res recebe o resultado
                                String res = RPN.calculadorClientHTTP(RPN.operations);
                                //o resultado é armazenado pois poderá ser usado na próxima operação.                 
                                stack.push(Double.parseDouble(res));
                                RPN.operations = "";
                                
			}else if(token.equals("^")){
				System.out.print("Operate ^\t\t");
				double secondOperand = stack.pop();
				double firstOperand = stack.pop();
                                //stack.push(math.pow(firstOperand, secondOperand));
				//Neste ponto a expressão tem os 3 parametros e é montada a expressão para
                                //chamar o servidor PHP.
                                RPN.operations = RPN.operations+"oper1="+firstOperand+"&oper2="+secondOperand+"&operacao=5\r\n";
                                //Aqui é feita a requisição do cliente com a operação no formato:
                                //oper1=val1&oper2=val2&operacao=tipo
                                //res recebe o resultado
                                String res = RPN.calculadorClientHTTP(RPN.operations);
                                //o resultado é armazenado pois poderá ser usado na próxima operação.                 
                                stack.push(Double.parseDouble(res));
                                RPN.operations = "";
                                
			}else{//just in case
				System.out.println("Error");
				return;
			}
			System.out.println(stack);
		}
                RPN.result = stack.pop().toString();
		System.out.println("Final answer: " + RPN.result);
                
	}
 
	private static String cleanExpr(String expr){
		//remove all non-operators, non-whitespace, and non digit chars
		return expr.replaceAll("[^\\^\\*\\+\\-\\d/\\s]", "");
	}
        
        /**
         * Método que envia uma msg no formato oper1=val1&oper2=val2&operacao=tipo
         * para o servidor PHP que retorna o resultado.
         * @param msg string no formato oper1=val1&oper2=val2&operacao=tipo
         * @return string result com o resultado da operação.
         */
        private static String calculadorClientHTTP(String msg)
        {
            String result="";
            try {

                //URL do servidor PHP que retorna o resultado da operação.
                URL url = new URL("https://double-nirvana-273602.appspot.com/?hl=pt-BR");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true) ;

                //ENVIO DOS PARAMETROS
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                //Neste ponto a msg é enviada ao servidor.
                writer.write(msg); //1-somar 2-subtrair 3-multiplicar 4-dividir
                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    //RECBIMENTO DOS PARAMETROS
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "utf-8"));
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    //Neste ponto a reposta é recebido pela aplicação.
                    result = response.toString();
                    System.out.println("Resposta do Servidor PHP="+result);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
                    //Aqui a string é retornada.
                    return result;
        }
}
