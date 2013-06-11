/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cuentahogar;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author fernando
 */
public class Main {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
         java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
                try {
                    ElementosDinamicos ed = new ElementosDinamicos();
                    HashMap<String, String> datosIni = new HashMap<String, String>();
                    boolean entrarDirecto = false;
                    
                    String direccion = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("CuentaHogar.jar", "") + "cuentaHogar.ini";
                    File fichero = new File(direccion);

                    if (!fichero.exists()) {
                        VentanaInicial vIni = new VentanaInicial();
                        vIni.setVisible(true);
                    } else {
                        
                        datosIni = ed.leerIni();
                        
                        if(datosIni.get("volverMostrar").equals("no")){
                            ElementosGraficos eg = new ElementosGraficos(datosIni.get("BBDD"), datosIni.get("Usuario"), datosIni.get("Clave"));
                            eg.setVisible(true);
                        } else {
                            VentanaInicial vIni = new VentanaInicial();
                            vIni.setVisible(true);
                        }
                    } 

                } catch (Exception ex) {
                    Logger.getLogger(ElementosGraficos.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, "FALLO GRAVE!!\n Imposible arrancar CuentaHogar", "ERROR", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                    
                }
            }
        });
    } 
    
}
