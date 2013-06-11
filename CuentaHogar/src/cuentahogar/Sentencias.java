package cuentahogar;

import java.text.DecimalFormat;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Clase Sentencias (Elementos que gestionan las solicitudes del programa a la
 * bbdd)
 *
 * @author Fernando J. Gonzalez Lopez
 * @version 0.1
 */
public class Sentencias {

//***********************************VARIABLES***********************************//       
    //Variables
    private ConexionBBDD conectionBBDD;
    private ElementosDinamicos ed;
    private static final String G = "Gastos";
    private static final String I = "Ingresos";
    private static final String T = "Traspaso";
    private static final String SINFILTRO = "";
    private static final String NombreCta = "NOMBRE";
    private static final String NumeroCta = "NUMERO";

    /**
     * Constructor vacio
     */
    public Sentencias() {
    }

    /**
     * Constructor con parametros (bbdd, login y password)
     *
     * @param bbdd
     * @param login
     * @param password
     */
    public Sentencias(String bbdd, String login, String password) {
        this.conectionBBDD = new ConexionBBDD(bbdd, login, password);
        this.ed = new ElementosDinamicos(bbdd, login, password);
    }

    /**
     * Metodo que devuelve el parametro G (Gastos)
     *
     * @return Sting
     */
    public static String getG() {
        return G;
    }

    /**
     * Metodo que devuelve el parametro I (Ingresos)
     *
     * @return String
     */
    public static String getI() {
        return I;
    }

    /**
     * Metodo que devuelve el parametro T (Traspasos)
     *
     * @return String
     */
    public static String getT() {
        return T;
    }

    /**
     * Metodo que devuelve el parametro "" (SinFiltro)
     *
     * @return String
     */
    public static String getSinFiltro() {
        return SINFILTRO;
    }

    /**
     * Metodo que devuelve el parametro NOMBRE (NombreCta)
     *
     * @return String
     */
    public static String getNombreCta() {
        return NombreCta;
    }

    /**
     * Metodo que devuelve el parametro NUMERO (NumeroCta)
     *
     * @return String
     */
    public static String getNumeroCta() {
        return NumeroCta;
    }

    //***********************************SENTENCIAS GENERALES***********************************//  
    /**
     * Metodo para extraer el ID de una Cuenta
     *
     * @param cuenta
     * @return int
     * @throws Exception
     */
    private int solicitaIdCta(String cuenta) throws Exception {

        int idCuenta;

        idCuenta = ed.solicitaId(
                "SELECT ID_CUENTA "
                + "FROM CUENTAS "
                + "WHERE NOMBRE ='" + cuenta + "';");

        return idCuenta;
    }

    /**
     * Metodo para sumar los valores de una columna con elementos numéricos
     *
     * @param tabla
     * @param campoTexto
     */
    private void sumarColumna(JTable tabla, JTextField campoTexto) {

        //Delaramos el formato de numero (con 2 decimales)
        DecimalFormat df = new DecimalFormat("0.00");

        //Recorremos la columna y sumamos sus valores
        float aux = 0;
        for (int i = 0; i < tabla.getRowCount(); i++) {
            aux = aux + Float.parseFloat((String) tabla.getValueAt(i, 4));
        }

        //Insertamos el valor en el campo de texto solicitado
        campoTexto.setText(String.valueOf(df.format(aux)));
    }

    /**
     * Metodo que actualiza los capitales de las cuentas
     *
     * @param importe
     * @param idCuenta
     * @param idMovimiento
     * @throws Exception
     */
    private boolean actualizarCapitalCtas(String importe, int idCuenta, int idMovimiento) throws Exception {

        float capital = -1;
        String aux;

        conectionBBDD.ejecutarSentencia(
                "UPDATE CUENTAS"
                + " SET"
                + " CAPITAL = CAPITAL +" + importe 
                + " WHERE ID_CUENTA = " + idCuenta + ";");

        conectionBBDD.ejecutarSentencia(
                "UPDATE CUENTAS"
                + " SET"
                + " CAPITAL = CAPITAL -" + importe 
                + " WHERE ID_CUENTA = " + idMovimiento + ";");
        
        aux = ed.solicitaElemento(
                "SELECT CAPITAL"
                + " FROM CUENTAS"
                + " WHERE ID_CUENTA = " + idCuenta + ";");
        
        if (aux != null){
            capital = Float.parseFloat(aux);
        }

        if (capital < 0 && aux != null) {
            return false;
        } else {
            return true;
        }
    }

    //***********************************SENTENCIAS ESPECIFICAS PARA LA TABLA MOVIMIENTOS***********************************//     
    /**
     * Metodo que carga la tabla de tipo de movimiento y los desplegables del
     * programa correspondientes con los datos de los tipos de movmientos de la
     * bbdd en funcion a un filtro
     *
     * @param tipo
     * @param filtro
     * @param tabla
     * @throws Exception
     */
    public void cargarListaMov(String tipo, String filtro, JTable tabla, JTextField campoTxtTotal, boolean ordenable, boolean sumTotal) throws Exception {
        //En funcion del tipo de movimiento se modificaran los titulos de las columnas de la tabla
        String campo1 = "Cuenta";
        String campo2 = tipo;
        int signo = -1;
        if (tipo.equals(T)) {
            campo1 = "Cuenta Origen";
            campo2 = "Cuenta Destino";
        } else if (tipo.equals(I)){
            signo = 1;
        }

        //Pasamos la consulta que queremos cargar, el tipo de movimiento, el filtro que necesitemos y la tabla donde queremos que se cargue la consulta
        ed.pasarConsultaTabla(
                "SELECT M.ID_MOVIMIENTO 'Id', C1.NOMBRE '" + campo1 + "', C2.NOMBRE '" + campo2 + "', DATE_FORMAT(M.FECHA,'%d-%m-%Y') 'Fecha', M.IMPORTE*" + signo + " 'Importe' "
                + "FROM CUENTAS C1, CUENTAS C2, MOVIMIENTOS M "
                + "WHERE C1.ID_CUENTA = M.ID_CUENTA_G "
                + "AND C2.ID_CUENTA = M.ID_CUENTA_I "
                + "AND M.TIPO = '" + tipo.charAt(0) + "' "
                + filtro
                + "ORDER BY M.FECHA DESC;", tabla, ordenable);

        //Escondemos la columna del id
        ed.esconderColumnaTabla(tabla, 0);

        //Alineamos las columnas como consideramos oportuno
        DefaultTableCellRenderer dtcr1 = new DefaultTableCellRenderer();
        dtcr1.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.getColumnModel().getColumn(3).setCellRenderer(dtcr1);

        DefaultTableCellRenderer dtcr2 = new DefaultTableCellRenderer();
        dtcr2.setHorizontalAlignment(SwingConstants.RIGHT);
        tabla.getColumnModel().getColumn(4).setCellRenderer(dtcr2);

        if (sumTotal) {
            this.sumarColumna(tabla, campoTxtTotal);
        }

    }

    /**
     * Metodo que comprueba si existe un Movimiento repetido en nuestra bbdd
     * (true = existe; false = no existe)
     *
     * @param tipo
     * @param valores
     * @return boolean
     * @throws Exception
     */
    public boolean comprobarMovID(String tipo, String[] valores) throws Exception {

        //Variables
        int id = 0;
        int idCuenta;
        int idMovimiento;
        String cuenta = valores[1];
        String movimiento = valores[2];
        String fecha = valores[3];
        String importe = valores[4];
        int signo = -1;
        
        if(tipo.equals(I)){
            signo = 1;
        }

        //Recuperamos el ID de la Cuenta
        idCuenta = this.solicitaIdCta(cuenta);

        //Recuperamos el ID del Movimiento
        idMovimiento = this.solicitaIdCta(movimiento);

        //Recuperamos el ID que necesitamos de la fila solicitada (utilizando ell idCuenta, el idMovimiento y los valores pasados por parametro)
        id = ed.solicitaId(
                "SELECT COUNT(ID_MOVIMIENTO) "
                + "FROM MOVIMIENTOS "
                + "WHERE ID_CUENTA_G = " + idCuenta
                + " AND ID_CUENTA_I = " + idMovimiento
                + " AND TIPO = '" + tipo.charAt(0) + "' "
                + "AND FECHA = '" + fecha + "' "
                + "AND IMPORTE = " + importe + "*" + signo + ";");

        //Si el id existe devolvemos true y si no existe devolvemos false
        if (id != 0) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Metodo para insertar un Movimiento en nuestra bbdd y refrescar la tabla
     * Movmientos
     *
     * @param tipo
     * @param id
     * @param valores
     * @param tabla
     * @throws Exception
     */
    public boolean insertarMov(String tipo, String[] valores, JTable tabla, JTextField campoTxtTotal) throws Exception {

        //Variables
        int idCuenta;
        int idMovimiento;
        String cuenta = valores[1];
        String movimiento = valores[2];
        String fecha = valores[3];
        String importe = valores[4];
        boolean capitalSuficiente = false;

        //Recuperamos el ID de la Cuenta
        idCuenta = this.solicitaIdCta(cuenta);

        //Recuperamos el ID del Movimiento
        idMovimiento = this.solicitaIdCta(movimiento);

        //Si es un ingreso sera importe positivo
        if (tipo.equals(this.getI())) {
            importe = importe.replace("-", "");
        } else {
            importe = "-" + importe.replace("-", "");
        }

        //Insertamos en la bbdd el nuevo Movimiento
        conectionBBDD.ejecutarSentencia(
                "INSERT INTO MOVIMIENTOS (ID_CUENTA_G, ID_CUENTA_I, TIPO, FECHA, IMPORTE) "
                + "VALUES (" + idCuenta + ", " + idMovimiento + ", '" + tipo.charAt(0) + "', '" + fecha + "', " + importe + ");");

        //Actualizamos el capital de las cuentas
        capitalSuficiente = this.actualizarCapitalCtas(importe, idCuenta, idMovimiento);

        //Recargamos nuestra tabla Movimientos
        this.cargarListaMov(tipo, SINFILTRO, tabla, campoTxtTotal, true, true);

        return capitalSuficiente;
    }

    /**
     * Metodo para eliminar un Movimiento en nuestra bbdd y refrescar la tabla
     * Movmientos
     *
     * @param tipo
     * @param id
     * @param tabla
     * @throws Exception
     */
    public void eliminarMov(String tipo, String[] valores, JTable tabla, JTextField campoTxtTotal) throws Exception {
        
        //Si el vector valores no esta vacio eliminamos la fila de nuestra bbdd
        if (valores != null) {
            //Variables
            int idCuenta;
            int idMovimiento;
            String cuenta = valores[1];
            String movimiento = valores[2];
            String importe = valores[4];
            
                        //Si es un ingreso sera importe positivo
            if (tipo.equals(this.getI())) {
                importe =  "-" + importe.replace("-", "");
            }

            //Recuperamos el ID de la Cuenta
            idCuenta = this.solicitaIdCta(cuenta);

            //Recuperamos el ID del Movimiento
            idMovimiento = this.solicitaIdCta(movimiento);

            conectionBBDD.ejecutarSentencia(
                    "DELETE FROM MOVIMIENTOS "
                    + "WHERE ID_MOVIMIENTO = " + valores[0] + ";");

            //Actualizamos el capital de las cuentas
            this.actualizarCapitalCtas(importe, idCuenta, idMovimiento);

        }
        //Recargamos nuestra tabla Movmimientos
        this.cargarListaMov(tipo, SINFILTRO, tabla, campoTxtTotal, true, true);
    }

    /**
     * Metodo para actualizar un Movimiento en nuestra bbdd y refrescar la tabla
     * Movmientos
     *
     * @param tipo
     * @param id
     * @param tabla
     * @throws Exception
     */
    public boolean editarMov(String tipo, JTable tabla, JTable tablaEd, JTextField campoTxtTotal) throws Exception {
        //Variables
        int idCuentaAntigua;
        int idMovimientoAntiguo;
        int idCuentaNueva;
        int idMovimientoNuevo;
        //Recuperamos valores
        String[] valoresAntiguos = ed.recuperarValoresFila(tabla, tabla.getSelectedRow());
        String[] valoresNuevos = ed.recuperarValoresFila(tablaEd, tablaEd.getSelectedRow());
        if (valoresNuevos != null) {
            int id = Integer.parseInt(valoresNuevos[0]);
            String cuentaNueva = valoresNuevos[1];
            String movimientoNuevo = valoresNuevos[2];
            String fechaNueva = valoresNuevos[3];
            String importeNuevo = valoresNuevos[4];

            String cuentaAntigua = valoresAntiguos[1];
            String movimientoAntiguo = valoresAntiguos[2];
            String importeAntiguo = valoresAntiguos[4];
            
            boolean capitalSuficiente = false;

            //Recuperamos el ID de la Cuenta y el Movimiento Nuevos
            idCuentaNueva = this.solicitaIdCta(cuentaNueva);
            idMovimientoNuevo = this.solicitaIdCta(movimientoNuevo);

            //Recuperamos el ID de la Cuenta y el Movimiento Antiguos
            idCuentaAntigua = this.solicitaIdCta(cuentaAntigua);
            idMovimientoAntiguo = this.solicitaIdCta(movimientoAntiguo);

            //Si es un ingreso sera importe positivo
            if (tipo.equals(this.getI())) {
                importeNuevo = importeNuevo.replace("-", "");
            } else {
                importeNuevo = "-" + importeNuevo.replace("-", "");
            }

            //Actualizamos en la bbdd el Movimiento
            conectionBBDD.ejecutarSentencia(
                    "UPDATE MOVIMIENTOS "
                    + "SET "
                    + "ID_CUENTA_G = " + idCuentaNueva + ", "
                    + "ID_CUENTA_I = " + idMovimientoNuevo + ", "
                    + "TIPO = '" + tipo.charAt(0) + "', "
                    + "FECHA = STR_TO_DATE('" + fechaNueva + "','%d-%m-%Y'), "
                    + "IMPORTE = " + importeNuevo
                    + " WHERE ID_MOVIMIENTO = " + id + ";");

            //Actualizamos el capital de las cuentas
            this.actualizarCapitalCtas(importeAntiguo, idCuentaAntigua, idMovimientoAntiguo);
            capitalSuficiente = this.actualizarCapitalCtas(importeNuevo, idCuentaNueva, idMovimientoNuevo);

            //Recargamos nuestra tabla Movimientos
            this.cargarListaMov(tipo, SINFILTRO, tabla, campoTxtTotal, true, true);
            
            return capitalSuficiente;
        } else {
            return true;
        }
    }

    //***********************************SENTENCIAS ESPECIFICAS PARA LA TABLA CUENTAS***********************************// 
    /**
     * Metodo que carga la tabla de cuentas y los desplegables del programa
     * correspondientes con los datos de las cuentas de la bbdd
     *
     * @param tabla
     * @param desp
     * @throws Exception
     */
    public void cargarTablaCuentas(JTable tabla, String filtro, JComboBox despCtas, JComboBox despTras1, JComboBox despTras2, boolean ordenable, boolean cargDesp) throws Exception {
        //Pasamos la consulta que queremos cargar y la tabla donde queremos que se cargue la consulta
        ed.pasarConsultaTabla("SELECT ID_CUENTA 'Id', NOMBRE 'Nombre', NUMERO 'Numero', CAPITAL 'Capital' "
                + "FROM CUENTAS "
                + "WHERE TIPO = 'C' "
                + filtro + ";", tabla, ordenable);

        //Pasamos la tabla de donde queremos extraer los datos, el desplegable donde los queremos cargar y la columna de la tabla que contiene los datos
        if (cargDesp) {
            ed.anyadirDesplegable(tabla, despCtas, 1);
            ed.anyadirDesplegable(tabla, despTras1, 1);
            ed.anyadirDesplegable(tabla, despTras2, 1);
        }

        //Alineamos las columnas como consideramos oportuno
        DefaultTableCellRenderer dtcr1 = new DefaultTableCellRenderer();
        dtcr1.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.getColumnModel().getColumn(2).setCellRenderer(dtcr1);

        DefaultTableCellRenderer dtcr2 = new DefaultTableCellRenderer();
        dtcr2.setHorizontalAlignment(SwingConstants.RIGHT);
        tabla.getColumnModel().getColumn(3).setCellRenderer(dtcr2);

        //Escondemos la columna del id
        ed.esconderColumnaTabla(tabla, 0);
    }

    /**
     * Metodo que comprueba si existe el Nombre o el Numero de Cuenta en nuestra
     * bbdd (true = existe; false = no existe)
     *
     * @param tipo
     * @param valores
     * @return boolean
     * @throws Exception
     */
    public boolean comprobarCta(String columna, String valor) throws Exception {
        //Recuperamos el ID que necesitamos de la fila solicitada (utilizando ell idCuenta, el idMovimiento y los valores pasados por parametro)
        int id = 0;
        
        id = ed.solicitaId(
                "SELECT COUNT(*) "
                + "FROM CUENTAS WHERE "
                + columna + " = '" + valor + "';");

        //Si el id existe devolvemos true y si no existe devolvemos false
        if (id == 0) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Metodo para insertar una Cuenta en nuestra bbdd y refrescar la tabla de
     * Cuentas y los desplegables correspondientes
     *
     * @param valores
     * @param tabla
     * @param despCtas
     * @param despTras1
     * @param despTras2
     * @param ordenable
     * @throws Exception
     */
    public void insertarCta(String[] valores, JTable tabla, JComboBox despCtas, JComboBox despTras1, JComboBox despTras2) throws Exception {
        //Variables
        String nombre = valores[1];
        String numeroCta = valores[2];
        String capital = valores[3];

        System.out.println(capital);

        //Si en numero de cuenta esta vacio es nulo sino le añadimos comillas
        if (numeroCta.isEmpty()) {
            numeroCta = null;
        } else if (numeroCta != null) {
            numeroCta = "'" + numeroCta + "'";
        }

        //Definimos capital '0.00' por defecto
        if (capital.equals("")) {
            capital = "0.00";
        }

        //Insertamos en la bbdd la nueva Cuenta
        conectionBBDD.ejecutarSentencia(
                "INSERT INTO CUENTAS (NOMBRE, TIPO, NUMERO, CAPITAL) "
                + "VALUES ('" + nombre + "', 'C', " + numeroCta + ", '" + capital + "');");

        //Recargamos nuestra tabla Cuentas
        this.cargarTablaCuentas(tabla, SINFILTRO, despCtas, despTras1, despTras2, true, true);

    }

    /**
     * Metodo para eliminar una Cuenta en nuestra bbdd y refrescar la tabla
     * Cuentas y los desplegables correspondientes
     *
     * @param tipo
     * @param valores
     * @param tabla
     * @param despCtas
     * @param despTras1
     * @param despTras2
     * @param ordenable
     * @throws Exception
     */
    public void eliminarCta(String tipo, String[] valores, JTable tabla, JComboBox despCtas, JComboBox despTras1, JComboBox despTras2) throws Exception {

        //Si el vector valores no esta vacio eliminamos la fila de nuestra bbdd
        if (valores[0] != null) {
            conectionBBDD.ejecutarSentencia(
                    "DELETE FROM CUENTAS "
                    + "WHERE ID_CUENTA = " + valores[0] + ";");

        }

        //Recargamos nuestra tabla Cuentas y los desplegables correspondientes
        this.cargarTablaCuentas(tabla, SINFILTRO, despCtas, despTras1, despTras2, true, true);

    }

    //String tipo, JTable tabla, JTable tablaEd, JTextField campoTxtTotal   
    public void editarCta(JTable tabla, JTable tablaEd, JComboBox despCtas, JComboBox despTras1, JComboBox despTras2) throws Exception {
        //Recuperamos los nuevos valores
        String[] valoresNuevos = ed.recuperarValoresFila(tablaEd, tablaEd.getSelectedRow());

        if (valoresNuevos != null) {
            int id = Integer.parseInt(valoresNuevos[0]);
            String nombre = valoresNuevos[1];
            String numeroCta = valoresNuevos[2];
            String capital = valoresNuevos[3];

            //Si en numero de cuenta esta vacio es nulo sino le añadimos comillas
            if (numeroCta.isEmpty()) {
                numeroCta = null;
            } else if (numeroCta != null) {
                numeroCta = "'" + numeroCta + "'";
            }

            //Definimos capital '0.00' por defecto
            if (capital.equals("")) {
                capital = "0.00";
            }

            //Actualizamos en la bbdd la Cuenta    
            conectionBBDD.ejecutarSentencia(
                    "UPDATE CUENTAS "
                    + "SET "
                    + "NOMBRE = '" + nombre + "', "
                    + "TIPO = 'C', "
                    + "NUMERO = " + numeroCta + ", "
                    + "CAPITAL = " + capital + " "
                    + "WHERE ID_CUENTA = " + id + ";");

            //Recargamos nuestra tabla Movimientos
            cargarTablaCuentas(tabla, SINFILTRO, despCtas, despTras1, despTras2, true, true);

        }
    }

    //***********************************SENTENCIAS ESPECIFICAS PARA LA TABLA TIPO MOVIMIENTOS***********************************//   
    /**
     * Metodo que carga la tabla de tipo de movimiento y los desplegables del
     * programa correspondientes con los datos de los tipos de movmientos de la
     * bbdd
     *
     * @param tipo
     * @param tabla
     * @param desp
     * @throws Exception
     */
    public void cargarTablaTipoMov(String tipo, String filtro, JTable tabla, JComboBox desp, boolean ordenable, boolean cargDesp) throws Exception {
        //Pasamos la consulta que queremos cargar, el tipo de movimiento que es y la tabla donde queremos que se cargue la consulta
        ed.pasarConsultaTabla(
                "SELECT ID_CUENTA 'Id', NOMBRE 'Nombre' "
                + "FROM CUENTAS "
                + "WHERE TIPO = '" + tipo.charAt(0) + "' "
                + filtro + ";", tabla, ordenable);

        //Pasamos la tabla de donde queremos extraer los datos, el desplegable donde los queremos cargar y la columna de la tabla que contiene los datos
        if (cargDesp) {
            ed.anyadirDesplegable(tabla, desp, 1);
        }

        //Escondemos la columna del id
        ed.esconderColumnaTabla(tabla, 0);
    }

    /**
     * Metodo para insertar un Tipo de Movimiento en nuestra bbdd y refrescar la
     * tabla de Tipos de Movimientos y los desplegables correspondientes
     *
     * @param nombre
     * @param tipo
     * @param tabla
     * @param desp
     * @param ordenable
     * @throws Exception
     */
    public void insertarTipoMov(String nombre, String tipo, JTable tabla, JComboBox desp) throws Exception {
        //Insertamos en la bbdd el nuevo Tipo de Movimiento
        conectionBBDD.ejecutarSentencia(
                "INSERT INTO CUENTAS (NOMBRE, TIPO) "
                + "VALUES ('" + nombre + "', '" + tipo.charAt(0) + "');");

        //Recargamos nuestra tabla de Tipos de Movimientos
        this.cargarTablaTipoMov(tipo, SINFILTRO, tabla, desp, true, true);
    }

    /**
     * Metodo para eliminar un Tipo de Movimiento en nuestra bbdd y refrescar la
     * tabla Movmientos y los desplegables correspondientes
     *
     * @param tipo
     * @param valores
     * @param tabla
     * @param desp
     * @param ordenable
     * @throws Exception
     */
    public void eliminarTipoMov(String tipo, String[] valores, JTable tabla, JComboBox desp) throws Exception {
        //Si el vector valores no esta vacio eliminamos la fila de nuestra bbdd
        if (valores[0] != null) {
            conectionBBDD.ejecutarSentencia(
                    "DELETE FROM CUENTAS "
                    + "WHERE ID_CUENTA = " + valores[0] + ";");
        }

        //Recargamos nuestra tabla Cuentas y los desplegables correspondientes
        this.cargarTablaTipoMov(tipo, SINFILTRO, tabla, desp, true, true);

    }

    public void editarTipoMov(String tipo, JTable tabla, JTable tablaEd, JComboBox desp) throws Exception {
        //Recuperamos los nuevos valores
        String[] valoresNuevos = ed.recuperarValoresFila(tablaEd, tablaEd.getSelectedRow());

        if (valoresNuevos != null) {
            int id = Integer.parseInt(valoresNuevos[0]);
            String nombre = valoresNuevos[1];

            //Actualizamos en la bbdd la Cuenta    
            conectionBBDD.ejecutarSentencia(
                    "UPDATE CUENTAS "
                    + "SET "
                    + "NOMBRE = '" + nombre + "' "
                    + "WHERE ID_CUENTA = " + id + ";");

            //Recargamos nuestra tabla Movimientos
            cargarTablaTipoMov(tipo, SINFILTRO, tabla, desp, true, true);

        }

    }

    //***********************************FILTROS***********************************// 
    /**
     * Metodo que regula el filtro de la Tabla Movimientos
     *
     * @param cuenta
     * @param movimiento
     * @param fecha
     * @param importe
     * @param tipo
     * @param tabla
     * @param condFecha
     * @param condImporte
     */
    public void cargarFiltroTM(JTextField cuenta, JTextField movimiento, JTextField dia, JTextField mes, JTextField anyo, JTextField importe, String tipo, JTable tabla, JComboBox condFecha, JComboBox condImporte, JTextField campoTxtTotal) throws Exception {
        //Recuperamos un String con los filtros
        String filtro = this.generarStringFiltroTM(cuenta, movimiento, dia, mes, anyo, importe, tipo, condFecha, condImporte);

        //cargamos la tabla con los filtros solicitados
        this.cargarListaMov(tipo, filtro, tabla, campoTxtTotal, true, true);
    }
    
    /**
     * Metodo que genera un String con los filtros
     * @param cuenta
     * @param movimiento
     * @param fecha
     * @param importe
     * @param condFecha
     * @param condImporte
     * @return
     * @throws Exception 
     */
    public String  generarStringFiltroTM (JTextField cuenta, JTextField movimiento, JTextField dia, JTextField mes, JTextField anyo, JTextField importe, String tipo, JComboBox condFecha, JComboBox condImporte) throws Exception{
        //Variables
        String filtroCuenta = "";
        String filtroMovimiento = "";
        String filtroFecha = "";
        String filtroImporte = "";
        String filtro = "";
        int signo = -1;

        //Controlamos el filtro por nombre de cuenta
        if (!cuenta.getText().isEmpty()) {
            filtroCuenta = "AND C1.NOMBRE LIKE '" + cuenta.getText() + "%' ";

        }
        //Controlamos el filtro por nombre de movimiento
        if (!movimiento.getText().isEmpty()) {
            filtroMovimiento = "AND C2.NOMBRE LIKE '" + movimiento.getText() + "%' ";
        }

        if (!dia.getText().isEmpty()) {
            filtroFecha = filtroFecha + "AND DATE_FORMAT(M.FECHA,'%d') " + condFecha.getSelectedItem() + " '" + dia.getText() + "' ";
        }
        
        if (!mes.getText().isEmpty() ) {
            filtroFecha = filtroFecha + "AND DATE_FORMAT(M.FECHA,'%m') " + condFecha.getSelectedItem() + " '" + mes.getText() + "' ";
        }
        
        if(!anyo.getText().isEmpty()) {
            filtroFecha = filtroFecha + "AND DATE_FORMAT(M.FECHA,'%Y') " + condFecha.getSelectedItem() + " '" + anyo.getText() + "' ";            
        }

        //Controlamos el filtro por importe
        if (!importe.getText().isEmpty()) {   
            if(tipo.equals(I)){
                signo = 1;
            }
            filtroImporte = "AND (M.IMPORTE*" + signo + ") " + condImporte.getSelectedItem() + " '" + importe.getText() + "' ";
        }

        //Juntamos todos los filtros
        filtro = filtroCuenta + filtroMovimiento + filtroFecha + filtroImporte;
        
        return filtro;
    }

    /**
     * Metodo para cargar la tabla Editar con los datos de la Tabla Movimientos
     * filtrando por ID
     *
     * @param tipo
     * @param id
     * @param tabla
     * @param ordenable
     * @throws Exception
     */
    public void cargarFiltroTEMov(String tipo, int id, JTable tabla, JTextField campoTxtTotal) throws Exception {
        //Mostramos el elemento a editar
        this.cargarListaMov(tipo, "AND M.ID_MOVIMIENTO = " + id + " ", tabla, campoTxtTotal, false, false);

    }

    /**
     * Metodo para cargar la tabla Editar con los datos de la Tabla Cuentas
     * filtrando por ID
     *
     * @param id
     * @param tabla
     * @param despCtas
     * @param despTras1
     * @param despTras2
     * @throws Exception
     */
    public void cargarFiltroTECta(int id, JTable tabla, JComboBox despCtas, JComboBox despTras1, JComboBox despTras2) throws Exception {
        this.cargarTablaCuentas(tabla, "AND ID_CUENTA = " + id, despCtas, despTras1, despTras2, false, false);
    }

    /**
     * Metodo para cargar la tabla Editar con los datos de la Tabla Tipo
     * Movimiento filtrando por ID
     *
     * @param id
     * @param tipo
     * @param filtro
     * @param tabla
     * @param desp
     * @throws Exception
     */
    public void cargarFiltroTETipoMov(int id, String tipo, JTable tabla, JComboBox desp) throws Exception {
        this.cargarTablaTipoMov(tipo, "AND ID_CUENTA = " + id, tabla, desp, false, false);
    }
}
