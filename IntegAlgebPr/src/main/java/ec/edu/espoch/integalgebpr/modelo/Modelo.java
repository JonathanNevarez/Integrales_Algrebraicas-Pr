package ec.edu.espoch.integalgebpr.modelo;

import ec.edu.espoch.integalgebpr.vista.Interfaz;
import reactor.core.publisher.Mono;

public class Modelo {
    private Interfaz vista;
    
    public Modelo(Interfaz vista) {
        this.vista = vista;
    }
    
    public Mono<String> procesarFuncion(String valorFuncion) {
        return Mono.fromCallable(() -> {
            String[] terminosSeparados;
            int numCoeficiente = 0;  
            int denCoeficiente = 0;  
            int numExponente = 0;
            int denExponente = 0;
            int newNumCoeficiente = 0; 
            boolean hayX;
            String resultadaTermino = "";
            String resultado = "";

            terminosSeparados = valorFuncion.split(" ");

            try {
                for (String term:terminosSeparados){
                    hayX = false;
                    if(term.contains("x")){
                        hayX = true;
                        String c = term.substring(0, term.indexOf("x"));
                        if (c.isEmpty() || c.equals("+")) {
                            numCoeficiente = 1;
                            denCoeficiente = 1;  
                        } else if (c.equals("-")) {
                            numCoeficiente = -1;
                            denCoeficiente = 1;
                        } else {
                            if(c.contains("/")){
                                String[] partes = c.split("/");
                                numCoeficiente = Integer.parseInt(partes[0]);
                                denCoeficiente = Integer.parseInt(partes[1]); 
                            }else{
                                numCoeficiente = Integer.parseInt(c);
                                denCoeficiente = 1;
                            }
                        }
                        if (term.contains("^")) {
                            String e = term.substring(term.indexOf("^") + 1);
                            if(e.contains("/")){
                                String[] partes = e.split("/");
                                numExponente = Integer.parseInt(partes[0]);
                                denExponente = Integer.parseInt(partes[1]); 
                            }else{
                                numExponente = Integer.parseInt(e);
                                denExponente = 1;
                            }
                        } else {
                            numExponente = 1;
                            denExponente = 1;
                        }  
                    }else{
                        hayX = false;
                        if(term.contains("/")){
                            String[] partes = term.split("/");
                            numCoeficiente = Integer.parseInt(partes[0]);
                            denCoeficiente = Integer.parseInt(partes[1]); 
                        }else{
                            numCoeficiente = Integer.parseInt(term);
                            denCoeficiente = 1;
                        }
                    }
                    if (hayX) {
                        newNumCoeficiente = numCoeficiente * denExponente;
                        int newNumExponente = numExponente + denExponente;
                        int newDenCoeficiente = denCoeficiente * newNumExponente;
                        int newDenExponente = denExponente;

                        int a = newNumCoeficiente;
                        int b = newDenCoeficiente;
                        while (b != 0) {
                            int temp = b;
                            b = a % b;
                            a = temp;
                        }
                        newNumCoeficiente /= a;
                        newDenCoeficiente /= a;

                        int a2 = newNumExponente;
                        int b2 = newDenExponente;
                        while (b2 != 0) {
                            int temp = b2;
                            b2 = a % b2;
                            a2 = temp;
                        }
                        newNumExponente /= a2;
                        newDenExponente /= a2;

                        newNumCoeficiente = (newDenCoeficiente < 0) ? -newNumCoeficiente : newNumCoeficiente*1;
                        newDenCoeficiente = (newDenCoeficiente < 0) ? -newDenCoeficiente : newDenCoeficiente;
                        newNumExponente = (newDenExponente < 0) ? -newNumExponente : newNumExponente;
                        newDenExponente = (newDenExponente < 0) ? -newDenExponente : newDenExponente;

                        String coeficienteParte = (newNumCoeficiente == 1) ? "" : (newNumCoeficiente == -1) ? "-" : newNumCoeficiente + "";
                        String xParte = (newNumExponente == 1) ? "x" : "x^" + newNumExponente;

                        if (newDenCoeficiente == 1) {
                            if(newDenExponente == 1){
                                resultadaTermino = coeficienteParte + xParte;
                            }else{
                                resultadaTermino = coeficienteParte + xParte + "/" + newDenExponente;

                            }
                        } else {
                            if(newDenExponente == 1){
                                resultadaTermino = "(" + coeficienteParte + xParte + ")/" + newDenCoeficiente;
                            }else{
                                resultadaTermino = "(" + coeficienteParte + xParte + "/" + newDenExponente + ")/" + newDenCoeficiente;
                            }
                        }
                    }else{
                        newNumCoeficiente = numCoeficiente;
                        resultadaTermino = (numCoeficiente == 1 && denCoeficiente == 1)? "x" : (numCoeficiente == -1 && denCoeficiente == 1 || numCoeficiente == 1 && denCoeficiente == -1)? "-x" : (numCoeficiente != 1 && denCoeficiente == 1)? numCoeficiente + "x" : numCoeficiente + "x/" + denCoeficiente; 
                    }
                    if(newNumCoeficiente >0){
                        resultado += "+" + resultadaTermino + "  ";   
                    }else{
                        String newResultadaTermino;
                        if(resultadaTermino.contains("(")){
                            newResultadaTermino = resultadaTermino.substring(0, 1)+ resultadaTermino.substring(2);
                        }else{
                            newResultadaTermino = resultadaTermino.substring(1);
                        }
                        resultado += "-" + newResultadaTermino + "  ";
                    }
                }
            } catch (NumberFormatException e) {
                resultado = "Error!! verifique la funcion ingresada";
            }
            return resultado;    
        });
    }
    
    public void mostrarvalor(String valorFuncion){   
        procesarFuncion(valorFuncion)
            .subscribe(  
                resultado -> this.vista.mostrarProcedimiendo1(resultado + " +C"),
                error -> System.err.println(error),
                () -> System.out.println("Fin del procesamiento")
            );
   } 
}
