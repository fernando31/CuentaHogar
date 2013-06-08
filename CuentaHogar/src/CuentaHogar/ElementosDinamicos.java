package CuentaHogar;

import com.toedter.calendar.JDateChooser;
import java.awt.Color;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 * Clase ElementosDinamicos (Metodos dinamicos utiles para cualquier programa)
 *
 * @author Fernando J. Gonzalez Lopez
 * @version 0.1
 */
public class ElementosDinamicos {

//***********************************VARIABLES***********************************//       
    //Variables
    private ConexionBBDD conectionBBDD;
    private Sentencias sent = new Sentencias();

    /**
     * Constructor vacio
     */
    public ElementosDinamicos() {
    }

    /**
     * Constructor con parametros (bbdd, login y password)
     *
     * @param bbdd
     * @param login
     * @param password
     */
    public ElementosDinamicos(String bbdd, String login, String password) {
        this.conectionBBDD = new ConexionBBDD(bbdd, login, password);
    }

//***********************************GESTION DE COMPONENTES GRAFICOS QUE CARGAN DATOS DE LA BBDD***********************************//     
    /**
     * Metodo que devuelve las tablas de la bbdd
     *
     * @return ArrayList<String>
     * @throws Exception
     */
    public ArrayList<String> solicitarTablasBBDD() throws Exception {
        //Variables
        ArrayList<String> tablas = new ArrayList<String>();
        String[] columnas = nombreColumnas("SHOW TABLES;");

        //Consultamos los nombres de las columnas de la tabla que nos introducen por parametro
        ResultSet rs = conectionBBDD.ejecutarConsulta("SHOW TABLES;");

        //Introducimos el nombre de las columnas de la tabla en el vector "campos"
        while (rs.next()) {
            tablas.add(rs.getString(columnas[0]));
        }

        conectionBBDD.desconectar();
        return tablas;
    }

    /**
     * Metodo para seleccionar los nombres de columnas de una tabla en funcion
     * de una consulta e introducirlos en un vector
     *
     * @param bbdd
     * @param login
     * @param password
     * @param tabla
     * @return String[]
     * @throws Exception
     */
    public String[] nombreColumnas(String consulta) throws Exception {
        //Vector donde introduciremos el nombre de las columnas de una consulta
        String[] columnas = null;
        //Cargamos el ResultSet con los resultados de una consulta
        ResultSet rs = conectionBBDD.ejecutarConsulta(consulta);
        //Dimensionamos el vector
        columnas = new String[rs.getMetaData().getColumnCount()];
        //Recorremos el vector y recogemos el Nombre de la columna del ResultSet
        for (int i = 0; i < columnas.length; i++) {
            columnas[i] = rs.getMetaData().getColumnName(i + 1);
        }
        //Desconectamos de la bbdd
        conectionBBDD.desconectar();
        //Devolvemos el vector con el nombre de las columnas
        return columnas;
    }

    /**
     * Metodo para consultar id de algun elemento de una tabla
     *
     * @param consulta
     * @return id
     * @throws Exception
     */
    public int solicitaId(String consulta) throws Exception {
        //Cargamos los nombres de las columnas en un vector
        String[] campos = this.nombreColumnas(consulta);
        //Inicializamos una variable int para el ID
        int id = 0;
        //Cargamos un ResultSet con la consulta
        ResultSet rs = conectionBBDD.ejecutarConsulta(consulta);
        //Recorremos el resulset y con un "for" del vector "campos" extraemos el ID de la 1ª Columna
        while (rs.next()) {
            for (int i = 0; i < campos.length; i++) {
                id = rs.getInt(campos[i]);
            }
        }
        //Desconectamos de la bbdd
        conectionBBDD.desconectar();
        //Devolvemos el ID
        return id;
    }
    
    /**
     * Metodo para consultar id de algun elemento de una tabla
     *
     * @param consulta
     * @return id
     * @throws Exception
     */
    public String solicitaElemento(String consulta) throws Exception {
        //Cargamos los nombres de las columnas en un vector
        String[] campos = this.nombreColumnas(consulta);
        //Inicializamos una variable int para el ID
        String elemento = null;
        //Cargamos un ResultSet con la consulta
        ResultSet rs = conectionBBDD.ejecutarConsulta(consulta);
        //Recorremos el resulset y con un "for" del vector "campos" extraemos el ID de la 1ª Columna
        while (rs.next()) {
            for (int i = 0; i < campos.length; i++) {
                elemento = rs.getString(campos[i]);
            }
        }
        //Desconectamos de la bbdd
        conectionBBDD.desconectar();
        //Devolvemos el ID
        return elemento;
    }    

    /**
     * Metodo que construye dinamicamente una JTable a partir de cualquier
     * consulta realizada a la BBDD
     *
     * @param consulta
     * @param tabla
     * @throws Exception
     */
    public void pasarConsultaTabla(String consulta, JTable tabla, boolean ordenable) throws Exception {
        //Variables
        String[] campos = this.nombreColumnas(consulta); //Extreamos el nombre de las columnas que devuelve la consulta
        String[] campo = new String[campos.length];
        DefaultTableModel dtm = new DefaultTableModel(null, campos);


        //Extraemos las filas que devuelve la consulta y las añadimos al DefaultTableModel
        ResultSet rs = conectionBBDD.ejecutarConsulta(consulta);
        String aux = "";
        while (rs.next()) {
            for (int i = 0; i < campos.length; i++) {
                aux = rs.getString(campos[i]);
                if (aux == null) {
                    aux = "";
                }
                campo[i] = aux;
            }
            dtm.addRow(campo);
        }

        //Desconectamos el link con la BBDD
        conectionBBDD.desconectar();

        //Introducimos el DefaultTableModel en la tabla
        tabla.setModel(dtm);

        //En el caso de que queramos una tabla ordenable utilizamos un TableRowSorter<TableModel>
        if (ordenable) {
            TableRowSorter<TableModel> trs = new TableRowSorter<TableModel>(dtm);
            tabla.setRowSorter(trs);
        }
    }

    /**
     * Metodo para añadir elementos de una columna de una tabla a un JComboBox
     *
     * @param tabla
     * @param desplegable
     */
    public void anyadirDesplegable(JTable tabla, JComboBox desplegable, int columna) {
        //Limpiamos los valores del JComboBox
        desplegable.removeAllItems();

        //Introducimos en el JComboBox los valores de cada fila de la columna introducida por parametros
        for (int i = 0; i < tabla.getRowCount(); i++) {
            desplegable.addItem(tabla.getValueAt(i, columna));
        }
    }

//***********************************GESTION COMPONENTES GRAFICOS INDEPENDIENTES A LA BBDD***********************************// 
    /**
     * Metodo que hace invisible una columna de una JTable
     *
     * @param tabla
     * @param columna
     */
    public void esconderColumnaTabla(JTable tabla, int columna) {
        //Establecemos el ancho de la columna a cero
        tabla.getColumnModel().getColumn(columna).setMaxWidth(0);
        tabla.getColumnModel().getColumn(columna).setMinWidth(0);
        tabla.getColumnModel().getColumn(columna).setPreferredWidth(0);
    }

    /**
     * Metodo para eliminar todas las filas de una tabla
     *
     * @param tabla
     * @param dtm
     */
    private void eliminarFilasTabla(JTable tabla, DefaultTableModel dtm) {
        //Recuperamos el DefaultTableModel
        dtm = (DefaultTableModel) tabla.getModel();

        //Eliminamos todas las filas desde al ultima a la primera
        for (int i = (dtm.getRowCount() - 1); i >= 0; i--) {
            dtm.removeRow(i);
        }
    }

    /**
     * Metodo para activar o desactivar cualquier elemento javax.swing.* y sus
     * componentes
     *
     * @param componente
     * @param estado
     */
    public void estadoComponente(JComponent componente, boolean estado) {
        //Activamos o desactivamos el elemento
        componente.setEnabled(estado);

        //Recorremos todos los componentes del elemento y los activamos o desactivamos
        for (int i = 0; i < componente.getComponents().length; i++) {
            componente.getComponent(i).setEnabled(estado);
        }
    }

    /**
     * Metodo que recupera los valores de la fila de una tabla
     *
     * @param tabla
     * @param fila
     * @return String[]
     */
    public String[] recuperarValoresFila(JTable tabla, int fila) {
        //Vector donde introduciremos los valores
        String[] valores = null;

        //Controlamos que se ha seleccionado al menos una fila
        //Recuperamos la fila seleccionada y el número de columnas de la tabla. Luego dimensionamos el vector
        int columnas = tabla.getColumnCount();
        valores = new String[columnas];
        //Recorremos la fila de la tabla e introducimos sus valores en el vector
        for (int i = 0; i < columnas; i++) {
            valores[i] = (String) tabla.getValueAt(fila, i);
        }

        //Devolvemos los valores
        return valores;

    }

    /**
     * Metodo para filtrar tablas por el contenido de sus filas
     *
     * @param tabla
     * @param campos
     */
    public void cargarFiltro(JTable tabla, String[] campos) {

        //Variables
        DefaultTableModel dtm = (DefaultTableModel) tabla.getModel();
        TableRowSorter<TableModel> trs = new TableRowSorter<TableModel>(dtm);
        tabla.setRowSorter(trs);

        //Introducimos en un Vector los filtros en funcion de las columnas
        ArrayList lista = new ArrayList();
        for (int i = 0; i < campos.length; i++) {
            lista.add(RowFilter.regexFilter("(?i).*" + campos[i] + ".*", i));
        }

        //Añadimos el tipo de filtro "and" que filtra en base todos los filtros creados
        RowFilter filtroAnd = RowFilter.andFilter(lista);

        /*
         //Otros ejemplos de filtro        
         LinkedList<RowFilter> lista1 = new LinkedList<RowFilter>();
         RowFilter filtroOr = RowFilter.orFilter(lista);
         RowFilter filtroNo = RowFilter.notFilter(lista1.getFirst());
         RowFilter.dateFilter(ComparisonType.BEFORE, new Date(),1);
         RowFilter.numberFilter(ComparisonType.EQUAL, 10, 0);
         */

        //Añadimos el tipo de filtro a la tabla
        trs.setRowFilter(filtroAnd);
    }

//***********************************INFORMES***********************************// 
    /**
     * Metodo para generar informes
     *
     * @param archivo
     * @throws Exception
     */
    public void generarReport(String archivo) throws Exception {
        //Conectamos a la bbdd
        Connection cn = conectionBBDD.conectar();
        //Inicializamos variables
        JasperReport reporte;
        JasperPrint imprimir;
        //Generamos el infrome a partir el archivo que nos pasen por parametro
        reporte = JasperCompileManager.compileReport(archivo);
        imprimir = JasperFillManager.fillReport(reporte, null, cn);
        //Mostramos el infrome (el false es para que no se cierre el programa al cerrar la ventana del informe)
        JasperViewer.viewReport(imprimir, false);
    }

    /**
     * Metodo que genera un informe a partir de una JTable
     *
     * @param panel
     * @param tabla
     * @param archivo
     * @throws Exception
     */
    public void imprimirTabla(JPanel panel, JTable tabla, String archivo, String total) throws Exception {

        //Variables
        DefaultTableModel dtmOrdenado = this.ordenarTabla(tabla);
        JRTableModelDataSource jrmds = new JRTableModelDataSource(dtmOrdenado);
        SimpleDateFormat dt = new SimpleDateFormat("yyyy/MM/dd");
        String fecha = dt.format(new Date());

        //Compilamos el informe
        JasperReport jr = JasperCompileManager.compileReport(archivo);

        //Utilizamos un HashMap para los parametros
        HashMap<String, Object> params = new HashMap<String, Object>();

        //Insertamos el TitleBorder, el total y la fecha en el HashMap y luego los elementos
        TitledBorder tb = (TitledBorder) panel.getBorder();
        params.put("Title", tb.getTitle());
        params.put("Total", total);
        params.put("Date", fecha);
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            params.put("Title" + i, tabla.getColumnName(i));
        }

        //Preparamos el informe
        JasperPrint jp = JasperFillManager.fillReport(jr, params, jrmds);

        //Mostramos e infrome (false para que la ventana cuando se cierre no cierre todo el programa)
        JasperViewer.viewReport(jp, false);


    }

    public void imprimirGrafica(String archivo, JTextField cuenta, JTextField movimiento, JTextField fecha, JTextField importe, JComboBox condFecha, JComboBox condImporte) throws Exception{
        //Conectamos a la bbdd
        Connection cn = conectionBBDD.conectar();
        //Inicializamos variables
        JasperReport reporte;
        JasperPrint imprimir;
        //Generamos el infrome a partir el archivo que nos pasen por parametro
        reporte = JasperCompileManager.compileReport(archivo);
        
        //Utilizamos un HashMap para los parametros
        HashMap<String, Object> params = new HashMap<String, Object>();
        
    /*    String filtro = sent.generarStringFiltroTM(cuenta, movimiento, fecha, importe, tipo, condFecha, condImporte);
        
        System.out.println(filtro);
        
        if(filtro == null){
            filtro = "";
        }
        
        params.put("Select", filtro);
        
        System.out.println(params.get("Select"));
       
    */    
        imprimir = JasperFillManager.fillReport(reporte, params, cn);
        //Mostramos el infrome (el false es para que no se cierre el programa al cerrar la ventana del informe)
        JasperViewer.viewReport(imprimir, false);
    }

    /**
     * Metodo para que JasperReport imprima la tabla segun el oden que tenga el
     * usuario en la tabla
     *
     * @param tabla
     * @return DefaultTableModel
     */
    private DefaultTableModel ordenarTabla(JTable tabla) {

        //Variables     
        String[] nombreColumnas = new String[tabla.getColumnCount()];
        String[] filaDatos = new String[tabla.getColumnCount()];
        DefaultTableModel dtmOrdenado = new DefaultTableModel(null, nombreColumnas);

        //Insertamos en la nueva tabla las filas en el orden correspondiente
        for (int i = 0; i < tabla.getRowCount(); i++) {
            for (int y = 0; y < tabla.getColumnCount(); y++) {
                filaDatos[y] = (String) tabla.getValueAt(i, y);
            }
            dtmOrdenado.addRow(filaDatos);
        }

        //Devolvemos el modelo
        return dtmOrdenado;
    }

//***********************************ERRORES***********************************//    
    /**
     * Metodo para mostrar por pantalla una ventana de error con el texto de la
     * Exception
     *
     * @param mensaje
     * @param ex
     */
    public void mostrarError(String mensaje, Exception ex) {
        //Variables
        JPanel panel = new JPanel();
        JLabel label = new javax.swing.JLabel();
        JScrollPane scrollPanel = new javax.swing.JScrollPane();
        JTextArea textArea = new javax.swing.JTextArea();

        //Introducimos el mensaje
        label.setText(mensaje);

        /*
         //Posibilidades de edicion del JTextArea y label
        
         Font newLabelFont = new Font("Serif", Font.BOLD, 18);  
         label.setFont(newLabelFont);
        
         textArea.setColumns(40);
         textArea.setRows(10);
         textArea.setBackground(Color.BLACK);
         textArea.setForeground(Color.BLUE);
         textArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
         */
        //Deshabilitamos el JTextArea e introducimos la Exception
        textArea.setEditable(false);
        textArea.setText(ex.getMessage());

        //Introducimos el JTextArea en un scrollPanel
        scrollPanel.setViewportView(textArea);

        //Declaramos un diseño vertical para que aparezca el label encima del textArea
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //Añadimos JLabel al JPanel con los espacios que consideramos
        panel.add(Box.createVerticalStrut(15));
        panel.add(label);

        //Añadimos el JScrollPanel con el JTextArea y los espacios que consideremos, siempre y cuando el JTextArea no este vacio
        if (!textArea.getText().isEmpty()) {
            panel.add(Box.createVerticalStrut(25));
            panel.add(scrollPanel);
        }

        //Mostramos un JOptionPane con el panel que hemos creado de error
        JOptionPane.showMessageDialog(null, panel, "ERROR", JOptionPane.ERROR_MESSAGE);
    }

//*********************************VALIDADORES**********************************//

    /**
     * Metodo para comprobar que un importe ha sido introducido correctamente
     *
     * @param textField
     * @return boolean
     */
    public boolean validarImporte(JTextField textField) {
        try {
            String aux = textField.getText();
            if (!this.comprobarFloat(aux, false)) {
                textField.setForeground(Color.RED);
                return false;
            } else {
                textField.setForeground(Color.BLACK);
                return true;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Metodo para comprobar que un numero de cuenta por partes ha sido
     * introducido correctamente
     *
     * @param textField
     * @return boolean
     */
    public boolean validarCta(JTextField textField, int longitud) {
        try {
            String aux = textField.getText();
            if (aux.length() != longitud) {
                textField.setForeground(Color.RED);
                return false;
            } else {
                if (!this.comprobarInteger(aux)) {
                    textField.setForeground(Color.RED);
                    return false;
                } else {
                    textField.setForeground(Color.BLACK);
                    return true;
                }
            }
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Metodo para validar el formato de un numero de cuenta
     *
     * @param numCta
     * @return
     */
    public boolean validarNumeroCta(String numCta) {
        //Utilizamos un patron con expresion regular
        Pattern patron = Pattern.compile("^[0-9]{4} [0-9]{2} [0-9]{4} [0-9]{10}$");
        //Declaramos un comprobador del patron
        Matcher encaja = patron.matcher(numCta);
        //Comprobamos que todo encaje
        if (!encaja.find()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Metodo para comprobar que una fecha ha sido introducido correctamente
     *
     * @param date
     * @return
     */
    public boolean validarFecha(JDateChooser date) {
        try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
            formatoFecha.format((Date) date.getDate());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Metodo que comprueba si un campo esta vacio
     *
     * @param aux
     * @return
     * @throws Exception
     */
    public boolean validarCampoVacio(JTextField textField) {
        String aux = textField.getText();
        try {
            if (this.comprobarCampoVacio(aux)) {
                textField.setForeground(Color.BLACK);
                return true;
            } else {
                textField.setForeground(Color.RED);
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }
    
    private boolean comprobarCampoVacio(String aux) throws Exception {

        if (!(aux.replace(" ", "")).isEmpty() && !aux.contains("'") && !aux.contains("\\")) {
            return true;
        } else {
            return false;
        }
    

    }

    /**
     * Metodo que comprueba si un string se puede pasar a float
     *
     * @param aux
     * @return
     * @throws Exception
     */
    public boolean comprobarFloat(String aux, boolean permiteNegativo) throws Exception {
        try {
            float i = Float.parseFloat(aux.replace(",", "."));
            if (!permiteNegativo && i < 0) {
                return false;
            } else {
                return true;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Metodo que comprueba si un string se puede pasar a integer
     *
     * @param aux
     * @return
     * @throws Exception
     */
    public boolean comprobarInteger(String aux) throws Exception {
        try {
            float i = Integer.parseInt(aux);
            if (i < 0) {
                return false;
            } else {
                return true;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Metodo que comprueba si una fecha se ha introducido correctamente
     *
     * @param aux
     * @return boolean
     * @throws Exception
     */
    public boolean comprobarFecha(String aux) throws Exception {
        //Declaramos un nuevo calendario gregoriano y fromato de fecha
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = new GregorianCalendar();
        c.setLenient(false);

        try {
            //Introducimos los parametros para establecer la fecha dividiendo el String "aux"
            c.set(Integer.parseInt(aux.split("-")[2]), Integer.parseInt(aux.split("-")[1]) - 1, Integer.parseInt(aux.split("-")[0]));
            formatoFecha.format(c.getTime());         
            return true;
            //Controlamos errores
        } catch (IllegalArgumentException e) {
            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }

    }

    /**
     * Metodo que compara si dos vectores son iguales
     *
     * @param vect1
     * @param vect2
     * @return boolean
     * @throws Exception
     */
    public boolean compararVectores(String[] vect1, String[] vect2) throws Exception {
        String aux1 = "";
        String aux2 = "";
        boolean iguales = false;
        int contador = 0;

        for (int i = 0; i < vect1.length; i++) {
            aux1 = vect1[i];
            aux2 = vect2[i];
            if (aux1.equals(aux2)) {
                contador++;
            }
        }

        if (vect1.length == contador) {
            iguales = true;
        }
        return iguales;
    }

//*********************************TRATAMIENTO DE FICHEROS**********************************//

    /**
     * Metodo para guardar un archivo de recuperacion
     *
     * @param titulo
     * @param funcion
     * @param extension
     * @throws Exception
     */
    public void guardar(String titulo, String funcion, String extension) throws Exception {

        //Creamos un selector de archivos
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle(titulo);

        //Filtramos por las extensiones pasadas por parametro
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivo " + extension.toLowerCase(), extension.toLowerCase());
        selector.setFileFilter(filtro);

        //Activamos el boton de guardar
        int resultado = selector.showDialog(null, funcion);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            //Recuperamos el fichero del selector de archivos
            File fichero = selector.getSelectedFile();

            //Controlamos si hay que sobreescribir 
            int respuesta = 0;
            if (fichero.exists()) {
                respuesta = JOptionPane.showConfirmDialog(null, "El fichero ya existe\n¿Desea sobreescribirlo?", titulo, JOptionPane.YES_NO_OPTION);
            }

            if (!fichero.exists() || respuesta == JOptionPane.YES_OPTION) {
                //Recuperamos el PATH
                String direccion = fichero.getAbsolutePath();

                //Controlamos la extension del archivo
                if (!(direccion.endsWith("." + extension.toLowerCase()))) {
                    direccion = direccion + "." + extension.toLowerCase();
                }

                //Generamos el fichero de recuperacion
                this.generarFicheroRecuperacion(titulo, direccion);

            } else if (respuesta == JOptionPane.NO_OPTION) {
                this.guardar(titulo, funcion, extension);
            }
        }
    }

    /**
     * Metodo para abrir un archivo de recuperacion
     *
     * @param titulo
     * @param funcion
     * @param extension
     * @throws Exception
     */
    public void abrir(String titulo, String funcion, String extension) throws Exception {

        //creamos una variable global para guardar el path
        String direccion = "";

        //Creamos un selector de archivos
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle(titulo);

        //Filtramos por las extensiones pasadas por parametro
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivo " + extension.toLowerCase(), extension.toLowerCase());
        selector.setFileFilter(filtro);

        //Activamos el boton de abrir
        int resultado = selector.showDialog(null, funcion);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            //Recuperamos el fichero del selector de archivos
            direccion = selector.getSelectedFile().getAbsolutePath();

            if (direccion.endsWith("." + extension.toLowerCase())) {
                this.cargarFicheroRecuperacion(titulo, direccion);
            } else {
                JOptionPane.showMessageDialog(null, "Archivo no soportado", titulo, JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    /**
     * Metodo que genera un script con todos los elementos de todas las tablas
     *
     * @param direccion
     * @throws Exception
     */
    private void generarFicheroRecuperacion(String titulo, String direccion) throws Exception {

        //Variables
        FileWriter fichero = new FileWriter(direccion, false);
        PrintWriter imprime = new PrintWriter(fichero);
        ResultSet rs = null;
        String aux = "";
        String valor = "";
        ArrayList<String> tablas = this.solicitarTablasBBDD();

        //Recorremos le vector tablas
        for (int i = 0; i < tablas.size(); i++) {
            String[] columnas = this.nombreColumnas("SELECT * FROM " + tablas.get(i) + ";");
            //Solicitamos la información de la tabla correspondiente
            rs = conectionBBDD.ejecutarConsulta("SELECT * FROM " + tablas.get(i) + ";");
            //Recorremos los resultados de la "select" y le damos formato a los valores
            while (rs.next()) {
                aux = "(";
                for (int f = 0; f < columnas.length; f++) {
                    valor = rs.getString(columnas[f]);
                    if (valor != null) {
                        valor = "'" + valor + "'";
                    }
                    aux = aux + valor;
                    if (f < columnas.length - 1) {
                        aux = aux + ",";
                    } else {
                        aux = aux + ")";
                    }
                }
                //Cuando ya tenemos el formato de los valores que necesitamos escribimos una linea en el fichero con el formato insert
                imprime.println("INSERT INTO " + tablas.get(i) + " VALUES " + aux + ";");
            }
        }

        //Desconectamos de la bbdd y cerramos el fichero
        conectionBBDD.desconectar();
        fichero.close();

        JOptionPane.showMessageDialog(null, "Archivo de recuperacion generado con exito", titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Metodo que borra el contenido de las tablas de la bbdd y carga un script
     * de datos
     *
     * @param direccion
     * @throws Exception
     */
    private void cargarFicheroRecuperacion(String titulo, String direccion) throws Exception {

        //Variables
        FileReader leefichero = new FileReader(direccion);
        BufferedReader leelinea = new BufferedReader(leefichero);
        String linea = "";
        ArrayList<String> tablas = this.solicitarTablasBBDD();

        //Recorremos le vector tablas y vamos borrando contenidos
        for (int i = 0; i < tablas.size(); i++) {
            conectionBBDD.ejecutarSentencia("DELETE FROM " + tablas.get(i) + ";");
        }

        //Vamos leyendo lineas (que tiene formato de insert) y las vamos ejecutando
        while ((linea = leelinea.readLine()) != null) {
            //Cargamos datos en la bbdd
            if (!linea.isEmpty()) {
                conectionBBDD.ejecutarSentencia(linea);
            }
        }

        //Cerramos el fichero
        leefichero.close();

        JOptionPane.showMessageDialog(null, "Datos recuperados con exito", titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Metodo que abre un fichero a partir de su direccion
     * @param direccion
     * @throws Exception 
     */
    public static void abrirFichero(String direccion) throws Exception {
        Desktop desktop;
        
        File file = new File(direccion);
        
        if (Desktop.isDesktopSupported()) { 
            desktop = Desktop.getDesktop(); 
            try {
                desktop.open(file);
            } catch (IOException ex) {
                Logger.getLogger(CuentaHogar.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Lo lamento,no se puede abrir el archivo; ésta Maquina no soporta la API Desktop");
        }
    }   
 
    /**
     * Metodo que abre una pagina web a partir de su direccion
     * @param uri
     * @throws Exception 
     */
    public static void abrirPaginaWeb(URI uri) throws Exception {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(uri);
        }
    }
}
