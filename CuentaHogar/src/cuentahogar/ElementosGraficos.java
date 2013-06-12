package cuentahogar;

import com.toedter.calendar.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * Clase ElementosGraficos (Parte grafica y control de datos del programa principal)
 *
 * @author Fernando J. Gonzalez Lopez
 * @version 0.1
 */
public class ElementosGraficos extends javax.swing.JFrame {

//***********************************VARIABLES***********************************//   

    private ConexionBBDD conectionBBDD;
    private ElementosDinamicos ed;
    private Sentencias sent;
    private String tipoMov;
    private static final String B2PG = "btn2pG";
    private static final String B2PI = "btn2pI";
    private static final String B2PT = "btn2pT";
    private static final String B2PTM = "btn2pTM";
    private static final String B2PFTM = "btn2pFiltroTM";

    private String getTipoMov() {
        return tipoMov;
    }

    private void setTipoMov(String TipoMov) {
        this.tipoMov = TipoMov;
    }

//***********************************CONSTRUCTOR ENTORNO GRAFICO***********************************// 
    /**
     * Constructor ElementosGraficos
     */
    public ElementosGraficos(String bbdd, String login, String password) {

        conectionBBDD = new ConexionBBDD(bbdd, login, password);
        ed = new ElementosDinamicos(bbdd, login, password);
        sent = new Sentencias(bbdd, login, password);
        
        this.dimensionarVentanas();

        try {
            this.conectarBBDD();
        } catch (Exception ex) {
            Logger.getLogger(ElementosGraficos.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

    /**
     * Metodo para dimensionar ventanas y arrancar componentes
     */
    private void dimensionarVentanas() {
        //Tomamos la dimension de la pantalla para centrar las ventanasos
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

        //Dimensionamos  y situamos ventana Principal, controlamos el cierre e incializamos
        this.setLocation(d.width / 2 - (d.width / 2) / 2, d.height / 2 - ((d.height / 2)) / 2);
        this.cierreVent(this);
        initComponents();

        //Dimensionamos, situamos ventana Cuentas y controlamos el cierre
        int anchoVC = 680;
        int altoVC = 345;
        vCtas.setSize(anchoVC, altoVC);
        vCtas.setLocation(d.width / 2 - anchoVC / 2, d.height / 2 - altoVC / 2);
        this.cierreVent(vCtas);

        //Dimensionamos, situamos Tipo Mov y controlamos el cierre
        int anchoVTMov = 600;
        int altoVTMov = 430;
        vTMov.setSize(anchoVTMov, altoVTMov);
        vTMov.setLocation(d.width / 2 - anchoVTMov / 2, d.height / 2 - altoVTMov / 2);
        this.cierreVent(vTMov);

        //Dimensionamos, situamos Editar y controlamos el cierre
        int anchoVE = 545;
        int altoVE = 160;
        vEditar.setSize(anchoVE, altoVE);
        vEditar.setLocation(d.width / 2 - anchoVE / 2, d.height / 2 - altoVE / 2);
        this.cierreVent(vEditar);
        
        //Dimensionamos, situamos Editar y controlamos el cierre
        int anchoVA = 450;
        int altoVA = 470;
        vAcercaDe.setSize(anchoVA, altoVA);
        vAcercaDe.setLocation(d.width / 2 - anchoVA / 2, d.height / 2 - altoVA / 2);
        this.cierreVent(vAcercaDe);
    }

    /**
     * Metodo para detectar si hay conexion con la bbdd y, si la hay, cargar la
     * aplicacion
     *
     * @throws Exception
     */
    private void conectarBBDD() throws Exception {

        //Comprobamos la conexion y si exite activamos la aplicacion
        if (this.comprobarConexion()) {
            //Habilita elementos
            barraMenu.setEnabled(true);
            ed.estadoComponente(barraMenu, true);
            barraHerramientas.setEnabled(true);
            btn2pTM.setVisible(false);
            btn2pTM.setFocusable(false);
            ed.estadoComponente(barraHerramientas, true);

            btn2pFiltroTM.setEnabled(true);
            pFiltro.setEnabled(true);
            pFiltro.setVisible(false);

            btnGraficaTM.setEnabled(true);
            btnImprimirTM.setEnabled(true);
            btn2pG.setSelected(true);
            this.gestionarBtns2p(B2PG);

            //Carga tablas
            sent.cargarListaMov(this.getTipoMov(), sent.getSinFiltro(), tMov, txtTotal, true, true);
            sent.cargarTablaCuentas(tablaCuentasVC, sent.getSinFiltro(), despCtas, despTrasOrigen, despTrasDest, true, true);
            sent.cargarTablaTipoMov(sent.getG(), sent.getSinFiltro(), tTipoMovVM, despMov, true, true);

            //Controla doble click en tablas
            this.controlarDobleClickTabla(tMov);
            this.controlarDobleClickTabla(tablaCuentasVC);
            this.controlarDobleClickTabla(tTipoMovVM);

        } else {
            conectionBBDD.desconectar();
        }
    }

    /**
     * Metodo para comprobar la conexion con la bbdd
     *
     * @return link
     * @throws Exception
     */
    private boolean comprobarConexion() throws Exception {
        try {
            Connection link = conectionBBDD.conectar();
            return true;
        } catch (Exception ex) {
            Logger.getLogger(ElementosGraficos.class.getName()).log(Level.SEVERE, null, ex);
            ed.mostrarError("Fallo al conectar con la BBDD", ex);
            return false;
        }

    }

//***********************************GESTION DE BOTONES DEL ENTORNO GRAFICO***********************************// 
//------------------------------VENTANA PRINCIPAL---------------------------------//  
    /**
     * Metodo que gestiona todos los botones de 2 posiciones de la ventana
     * principal
     *
     * @param boton
     */
    private void gestionarBtns2p(String boton) {
        try {
            if (!vEditar.isVisible()) {
                switch (boton) {
                    case "btn2pG":
                        this.gestionarBtn2pG(); //Gastos
                        break;
                    case "btn2pI":
                        this.gestionarBtn2pI(); //Ingresos
                        break;
                    case "btn2pT":
                        this.gestionarBtn2pT(); //Traspasos
                        break;
                    case "btn2pTM":
                        this.gestionarBtn2pTM(); //Tabla Movimientos
                        break;
                    case "btn2pFiltroTM": //Buscar
                        sent.cargarListaMov(this.getTipoMov(),  sent.getSinFiltro(), tMov, txtTotal, true, true);
                        this.gestionarBtn2pFiltroTM();
                        break;
                    default:
                        Logger.getLogger(ElementosGraficos.class.getName()).log(Level.SEVERE, "gestionarBtns2p(String boton);");
                        break;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Ventana de Edición abierta");
            }
        } catch (Exception ex) {
            Logger.getLogger(ElementosGraficos.class.getName()).log(Level.SEVERE, null, ex);
            ed.mostrarError("Fallo al gestionar Bontones", ex);
        }
    }

    /**
     * Metodo que habilita o deshabilita los botones de 2 posiciones de la
     * ventana principal
     *
     * @param estado
     */
    private void estadoBtn2p(boolean estado) {
        btn2pG.setEnabled(estado);
        btn2pI.setEnabled(estado);
        btn2pT.setEnabled(estado);
        btn2pTM.setEnabled(estado);
        btn2pFiltroTM.setEnabled(estado);
    }

    /**
     * Metodo que habilita el boton de 2 posiciones pasado por parametro
     *
     * @param btn2p
     */
    private void activarBtn2p(JToggleButton btn2p) {
        btn2pG.setSelected(false);
        btn2pI.setSelected(false);
        btn2pT.setSelected(false);
        btn2pTM.setSelected(false);
        btn2pFiltroTM.setSelected(false);
        btn2p.setSelected(true);

    }

    /**
     * Metodo que gestiona el boton de 2 posiciones de gastos de la ventana
     * principal
     *
     * @throws Exception
     */
    private void gestionarBtn2pG() throws Exception {
        this.setTipoMov(sent.getG());
        this.activarBtn2p(btn2pG);
        this.activarBtn2pG();
        this.desctivarBtn2pT();
        this.desactivarBtn2pTM();
        this.desactivarbtn2pFiltroTM();
    }

    /**
     * Metodo que activa los elementos necesarios cuando se pulsa el boton de 2
     * posiciones de gastos de la ventana principal
     *
     * @throws Exception
     */
    private void activarBtn2pG() throws Exception {
        ed.estadoComponente(pMov, true);
        pBajoMov.setBackground(Color.RED);
        pMov.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Gastos", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        etTipoMov.setText("Gasto");

        smG.setEnabled(false);
        smI.setEnabled(true);

        smAddTG.setEnabled(true);
        smAddTI.setEnabled(false);

        pTM.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Listado de Gastos", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        sent.cargarListaMov(this.getTipoMov(), sent.getSinFiltro(), tMov, txtTotal, true, true);

        this.cargarTablaTipoMov(sent.getG());
    }

    /**
     * Metodo que gestiona el boton de 2 posiciones de ingresos de la ventana
     * principal
     *
     * @throws Exception
     */
    private void gestionarBtn2pI() throws Exception {
        this.setTipoMov(sent.getI());
        this.activarBtn2p(btn2pI);
        this.activarBtn2pI();
        this.desctivarBtn2pT();
        this.desactivarBtn2pTM();
        this.desactivarbtn2pFiltroTM();
    }

    /**
     * Metodo que activa los elementos necesarios cuando se pulsa el boton de 2
     * posiciones de ingresos de la ventana principal
     *
     * @throws Exception
     */
    private void activarBtn2pI() throws Exception {
        ed.estadoComponente(pMov, true);
        pBajoMov.setBackground(Color.GREEN);
        pMov.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ingresos", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        etTipoMov.setText("Ingreso");

        smI.setEnabled(false);
        smG.setEnabled(true);

        smAddTI.setEnabled(true);
        smAddTG.setEnabled(false);

        pTM.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Listado de Ingresos", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        sent.cargarListaMov(this.getTipoMov(), sent.getSinFiltro(), tMov, txtTotal, true, true);

        this.cargarTablaTipoMov(sent.getI());
    }

    /**
     * Metodo que desactiva los elementos necesarios cuando se desactiva el
     * boton de 2 posiciones de gastos o ingresos
     *
     * @throws Exception
     */
    private void desactivarBtn2pG_I() throws Exception {
        ed.estadoComponente(pMov, false);
        pBajoMov.setBackground(Color.LIGHT_GRAY);

        smG.setEnabled(true);
        smI.setEnabled(true);
    }

    /**
     * Metodo que gestiona el boton de 2 posiciones de traspasos de la ventana
     * principal
     *
     * @throws Exception
     */
    private void gestionarBtn2pT() throws Exception {
        this.setTipoMov(sent.getT());
        this.activarBtn2p(btn2pT);
        this.activarBtn2pT();
        this.desactivarBtn2pG_I();
        this.desactivarBtn2pTM();
        this.desactivarbtn2pFiltroTM();
    }

    /**
     * Metodo que activa los elementos necesarios cuando se pulsa el boton de 2
     * posiciones de traspasos de la ventana principal
     *
     * @throws Exception
     */
    private void activarBtn2pT() throws Exception {
        ed.estadoComponente(pTras, true);
        pBajoTras.setBackground(Color.GRAY);

        smT.setEnabled(false);

        smAddCuenta.setEnabled(false);
        smAddTG.setEnabled(false);
        smAddTI.setEnabled(false);

        pTM.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Listado de Traspasos", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        sent.cargarListaMov(this.getTipoMov(), sent.getSinFiltro(), tMov, txtTotal, true, true);
    }

    /**
     * Metodo que desactiva los elementos necesarios cuando se desactiva el
     * boton de 2 posiciones de traspasos
     *
     * @throws Exception
     */
    private void desctivarBtn2pT() throws Exception {
        ed.estadoComponente(pTras, false);
        pBajoTras.setBackground(Color.LIGHT_GRAY);

        smT.setEnabled(true);
        smAddCuenta.setEnabled(true);
    }

    /**
     * Metodo que gestiona la activacion y desactivacion de la tabla movimientos
     *
     * @throws Exception
     */
    private void gestionarBtn2pTM() throws Exception {
        this.activarBtn2p(btn2pTM);
        this.activarBtn2pTM();
        this.desctivarBtn2pT();
        this.desactivarBtn2pG_I();
        this.desactivarbtn2pFiltroTM();
    }

    /**
     * Metodo que gestiona los elementos para la activacion de la tabla
     * movimientos
     *
     * @throws Exception
     */
    private void activarBtn2pTM() throws Exception {

        pTM.setEnabled(true);
        btnEditarTM.setEnabled(true);
        btnEliminarTM.setEnabled(true);
        pFiltro.setEnabled(false);
        ed.estadoComponente(pFiltro, false);

        tMov.setComponentPopupMenu(jPpMenuTablas);

        if (this.getTipoMov().equals(sent.getG())) {
            pBajoTM.setBackground(Color.RED);
        } else if (this.getTipoMov().equals(sent.getI())) {
            pBajoTM.setBackground(Color.GREEN);
        } else if (this.getTipoMov().equals(sent.getT())) {
            pBajoTM.setBackground(Color.GRAY);
        }

    }

    /**
     * Metodo que gestiona los elementos para la desactivacion de la tabla
     * movimientos
     *
     * @throws Exception
     */
    private void desactivarBtn2pTM() throws Exception {
        pTM.setEnabled(false);
        btnEditarTM.setEnabled(false);
        btnEliminarTM.setEnabled(false);
        btn2pFiltroTM.setEnabled(true);
        pFiltro.setEnabled(true);
        ed.estadoComponente(pFiltro, true);
        tMov.setComponentPopupMenu(null);

        pBajoTM.setBackground(Color.LIGHT_GRAY);
    }

    /**
     * Metodo que gestiona el boton de 2 posiciones de buscar de la ventana
     * principal
     *
     * @throws Exception
     */
    public void gestionarBtn2pFiltroTM() throws Exception {

        this.activarBtn2p(btn2pFiltroTM);
        this.activarbtn2pFiltroTM();
        this.desactivarBtn2pG_I();
        this.desctivarBtn2pT();
    }

    /**
     * Metodo que activa los elementos necesarios cuando se pulsa el boton de 2
     * posiciones de buscar de la ventana principal
     *
     * @throws Exception
     */
    private void activarbtn2pFiltroTM() throws Exception {
        ed.estadoComponente(pFiltro, true);
        pFiltro.setVisible(true);
        btn2pFiltroTM.setEnabled(false);
        btnEditarTM.setEnabled(false);
        btnEliminarTM.setEnabled(false);
        smFiltro.setEnabled(false);

        if (this.getTipoMov().equals(sent.getG())) {
            pBajoTM.setBackground(Color.RED);
        } else if (this.getTipoMov().equals(sent.getI())) {
            pBajoTM.setBackground(Color.GREEN);
        } else if (this.getTipoMov().equals(sent.getT())) {
            pBajoTM.setBackground(Color.GRAY);
        }
    }

    /**
     * Metodo que desactiva los elementos necesarios cuando se pulsa el boton de
     * 2 posiciones de traspasos de la ventana principal
     *
     * @throws Exception
     */
    private void desactivarbtn2pFiltroTM() throws Exception {
        ed.estadoComponente(pFiltro, false);
        pFiltro.setVisible(false);
        smFiltro.setEnabled(true);

        btn2pFiltroTM.setEnabled(true);

        this.cargarEtFiltroTM();
    }

    /**
     * Metodo que controla el cierre de la ventana principal
     */
    private void salirVentanaPrincipal() {
        int respuesta = JOptionPane.showConfirmDialog(null, "¿Realmente quiere salir del programa?", "SALIR", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

//------------------------------VENTANA CUENTAS---------------------------------//    
    /**
     * Metodo que inicializa la ventana de mantenimiento de cuentas
     */
    private void mostrarVentanaCtas() {
        checkNumCuentaVC.setSelected(false);
        this.estadoNumeroCta(false);
        vCtas.setVisible(true);

    }

    /**
     * Metodo que controla el cierre de la ventana de mantenimiento de cuentas
     */
    private void salirVentanaCtas() {
        this.desactivarTablaCtas();
        txtCuentaVC.setText("");
        txtCapitalInicialVC.setText("");
        txtNumCuentaVC1.setText("");
        txtNumCuentaVC1.setText("");
        txtNumCuentaVC1.setText("");
        txtNumCuentaVC1.setText("");
        if (vEditar.isVisible()) {
            this.salirVentanaEditarCta();
        }
        vCtas.dispose();
    }

    /**
     * Metodo que gestiona los elementos de la ventana de mantenimiento de
     * cuentas
     *
     * @param estado
     */
    private void estadoElementosCtas(boolean estado) {
        ed.estadoComponente(panelCuentasVC, !estado);
        btnEditarVC.setEnabled(estado);
        btnEliminarVC.setEnabled(estado);
        btnNuevaVC.setEnabled(estado);

        panelCuentasVC.setEnabled(true);
        btnSalirVC.setEnabled(true);
    }

    /**
     * Metodo que controla el checkbox del numero de cuenta de la ventana
     * mantenimiento de cuentas
     *
     * @param estado
     */
    private void estadoNumeroCta(boolean estado) {
        txtNumCuentaVC1.setText("");
        txtNumCuentaVC2.setText("");
        txtNumCuentaVC3.setText("");
        txtNumCuentaVC4.setText("");

        if (estado) {
            checkNumCuentaVC.setForeground(Color.BLACK);
        } else {
            checkNumCuentaVC.setForeground(Color.GRAY);
        }

        ed.estadoComponente(panelCuentasVCNumCta, estado);
    }

    /**
     * Metodo que gestiona la activacion de la tabla cuentas en la ventana de
     * mantenimiento de cuentas
     */
    private void activarTablaCtas() {
        if (!vEditar.isVisible()) {
            panelBajoCuentasVC.setBackground(Color.LIGHT_GRAY);
            panleBajoTablaCuentasVC.setBackground(Color.GRAY);

            tablaCuentasVC.setComponentPopupMenu(jPpMenuTablas);

            checkNumCuentaVC.setSelected(false);
            this.estadoNumeroCta(false);

            this.estadoElementosCtas(true);
        }
    }

    /**
     * Metodo que gestiona la desactivacion de la tabla cuentas en la ventana de
     * mantenimiento de cuentas
     */
    private void desactivarTablaCtas() {
        panelBajoCuentasVC.setBackground(Color.GRAY);
        panleBajoTablaCuentasVC.setBackground(Color.LIGHT_GRAY);

        tablaCuentasVC.setComponentPopupMenu(null);
        tablaCuentasVC.clearSelection();

        this.estadoElementosCtas(false);
    }

//------------------------------VENTANA MOVIMIENTOS---------------------------------//    
    /**
     * Metodo que inicializa la ventana de mantenimiento de tipos de movimiento
     */
    private void mostrarVentanaMov() {
        vTMov.setVisible(true);
    }

    /**
     * Metodo que controla el cierre de la ventana de mantenimiento de tipos de
     * movimiento
     */
    private void salirVentanaTipoMov() {
        this.desactivarTablaTipoMov();
        if (vEditar.isVisible()) {
            this.salirVentanaEditarTipoMov();
        }
        vTMov.dispose();
    }

    /**
     * Metodo que gestiona los elementos de la ventana de mantenimiento de tipos
     * de movimiento
     *
     * @param estado
     */
    private void estadoElementosMov(boolean estado) {
        ed.estadoComponente(pTMovVM, !estado);
        btnEditarVM.setEnabled(estado);
        btnEliminarVM.setEnabled(estado);
        btnNuevoVM.setEnabled(estado);

        pTMovVM.setEnabled(true);
        btnSalirVM.setEnabled(true);
    }

    /**
     * Metodo que gestiona la activacion de la tabla cuentas en la ventana de
     * mantenimiento de tipos de movimiento
     */
    private void activarTablaTipoMov() {
        if (!vEditar.isVisible()) {
            pBajoTMovVM.setBackground(Color.LIGHT_GRAY);

            if (this.getTipoMov().equals(sent.getG())) {
                pBajoTTMovVM.setBackground(Color.RED);
            } else if (this.getTipoMov().equals(sent.getI())) {
                pBajoTTMovVM.setBackground(Color.GREEN);
            }

            tTipoMovVM.setComponentPopupMenu(jPpMenuTablas);

            this.estadoElementosMov(true);
        }
    }

    /**
     * Metodo que gestiona la desactivacion de la tabla cuentas en la ventana de
     * mantenimiento de tipos de movimiento
     */
    private void desactivarTablaTipoMov() {

        if (this.getTipoMov().equals(sent.getG())) {
            pBajoTMovVM.setBackground(Color.RED);
        } else if (this.getTipoMov().equals(sent.getI())) {
            pBajoTMovVM.setBackground(Color.GREEN);
        }

        pBajoTTMovVM.setBackground(Color.LIGHT_GRAY);

        tTipoMovVM.setComponentPopupMenu(null);
        tTipoMovVM.clearSelection();

        this.estadoElementosMov(false);
    }

//------------------------------VENTANA EDITAR---------------------------------//     
    /**
     * Metodo que controla la carga de la ventana editar con datos de la tabla
     * movimientos
     *
     * @param tipo
     * @param tabla
     */
    public void cargarTablaEditarMov(String tipo, JTable tabla) {
        vEditar.setVisible(true);

        tabla.setEnabled(false);
        btnEliminarTM.setEnabled(false);
        btnEditarTM.setEnabled(false);
        tabla.setComponentPopupMenu(null);
        this.estadoBtn2p(false);

        String[] valores = ed.recuperarValoresFila(tabla, tabla.getSelectedRow());

        if (valores != null) {
            try {
                int id = Integer.parseInt(valores[0]);

                sent.cargarFiltroTEMov(tipo, id, tablaEditar, txtTotal);

                TableColumn cuenta = tablaEditar.getColumnModel().getColumn(1);
                JComboBox cuentas = new JComboBox();
                cuentas.removeAllItems();
                for (int i = 0; i < despCtas.getItemCount(); i++) {
                    cuentas.addItem(despCtas.getItemAt(i));
                }
                cuenta.setCellEditor(new DefaultCellEditor(cuentas));

                TableColumn movimiento = tablaEditar.getColumnModel().getColumn(2);
                JComboBox movimientos = new JComboBox();
                movimientos.removeAllItems();
                if (tipo.equals(sent.getG()) || tipo.equals(sent.getI())) {
                    for (int i = 0; i < despMov.getItemCount(); i++) {
                        movimientos.addItem(despMov.getItemAt(i));
                    }
                    movimiento.setCellEditor(new DefaultCellEditor(movimientos));
                } else if (tipo.equals(sent.getT())) {
                    for (int i = 0; i < despCtas.getItemCount(); i++) {
                        movimientos.addItem(despCtas.getItemAt(i));
                        movimiento.setCellEditor(new DefaultCellEditor(movimientos));

                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(ElementosGraficos.class.getName()).log(Level.SEVERE, null, ex);
                ed.mostrarError("Fallo al cargar Tabla de Editar Movimiento", ex);
            }
        }

    }

    /**
     * Metodo que controla el boton cancelar o salir de la ventana editar con
     * datos de la tabla movimientos
     */
    private void salirVentanaEditarMov() {
        tMov.setEnabled(true);
        btnEliminarTM.setEnabled(true);
        btnEditarTM.setEnabled(true);
        tMov.setComponentPopupMenu(jPpMenuTablas);
        vEditar.dispose();
        this.estadoBtn2p(true);
    }

    /**
     * Metodo que controla la carga de la ventana editar con datos de la tabla
     * cuentas
     *
     * @param tabla
     */
    private void cargarTablaEditarCta(JTable tabla) {
        vEditar.setVisible(true);

        tabla.setEnabled(false);
        btnEliminarVC.setEnabled(false);
        btnEditarVC.setEnabled(false);
        btnNuevaVC.setEnabled(false);
        tabla.setComponentPopupMenu(null);
        this.estadoBtn2p(false);

        String[] valores = ed.recuperarValoresFila(tabla, tabla.getSelectedRow());

        if (valores != null) {
            try {
                int id = Integer.parseInt(valores[0]);
                sent.cargarFiltroTECta(id, tablaEditar, despCtas, despTrasOrigen, despTrasDest);
            } catch (Exception ex) {
                Logger.getLogger(ElementosGraficos.class.getName()).log(Level.SEVERE, null, ex);
                ed.mostrarError("Fallo al cargar Tabla de Editar Cuenta", ex);
            }
        }
    }

    /**
     * Metodo que controla el boton cancelar o salir de la ventana editar con
     * datos de la tabla cuentas
     */
    private void salirVentanaEditarCta() {
        tablaCuentasVC.setEnabled(true);
        btnEliminarVC.setEnabled(true);
        btnEditarVC.setEnabled(true);
        btnNuevaVC.setEnabled(true);
        tablaCuentasVC.setComponentPopupMenu(jPpMenuTablas);
        vEditar.dispose();
        this.estadoBtn2p(true);
    }

    /**
     * Metodo que controla la carga de la ventana editar con datos de la tabla
     * tipo de movimiento
     *
     * @param tipo
     * @param tabla
     */
    private void cargarTablaEditarTipoMov(String tipo, JTable tabla) {
        vEditar.setVisible(true);

        tabla.setEnabled(false);
        btnEliminarVM.setEnabled(false);
        btnEditarVM.setEnabled(false);
        btnNuevoVM.setEnabled(false);
        tabla.setComponentPopupMenu(null);
        this.estadoBtn2p(false);

        String[] valores = ed.recuperarValoresFila(tabla, tabla.getSelectedRow());

        if (valores != null) {
            try {
                int id = Integer.parseInt(valores[0]);
                sent.cargarFiltroTETipoMov(id, this.getTipoMov(), tablaEditar, despMov);
            } catch (Exception ex) {
                Logger.getLogger(ElementosGraficos.class.getName()).log(Level.SEVERE, null, ex);
                ed.mostrarError("Fallo al cargar Tabla de Editar Tipo de Movimiento", ex);
            }
        }
    }

    /**
     * Metodo que controla el boton cancelar o salir de la ventana editar con
     * datos de la tabla tipo de movimiento
     */
    private void salirVentanaEditarTipoMov() {
        tTipoMovVM.setEnabled(true);
        btnEliminarVM.setEnabled(true);
        btnEditarVM.setEnabled(true);
        btnNuevoVM.setEnabled(true);
        tTipoMovVM.setComponentPopupMenu(jPpMenuTablas);
        vEditar.dispose();
        txtTMovVM.setText("");
        this.estadoBtn2p(true);
    }

//***********************************METODOS ESPECIFICOS DEL ENTORNO GRAFICO***********************************//
//------------------------------GENERALES---------------------------------//
    /**
     * Metodo que abre un selector de archivos y gestiona las funciones a
     * realizar
     *
     * @param titulo
     * @param funcion
     * @param extension
     */
    public void abrirSelectorArchivos(String funcion) {
        try {
            switch (funcion) {
                case "Guardar":
                    ed.guardar("Crear archivo de recuperación", "Guardar", "sql");
                    break;

                case "Abrir":
                    //Controlamos si hay que sobreescribir 
                    int respuesta = JOptionPane.showConfirmDialog(null, "Si carga un nuevo fichero\nperdera la informacion guardada actualmente\n¿Realmente desea continuar?", "Cargar archivo de recuperación", JOptionPane.YES_NO_OPTION);
                    if (respuesta == JFileChooser.APPROVE_OPTION) {
                        ed.abrir("Cargar archivo de recuperación", "Abrir", "sql");
                    }

                    sent.cargarListaMov(this.getTipoMov(), sent.getSinFiltro(), tMov, txtTotal, true, true);
                    if (vCtas.isVisible()) {
                        sent.cargarTablaCuentas(tablaCuentasVC, sent.getSinFiltro(), despCtas, despTrasOrigen, despTrasDest, true, true);
                    }

                    if (vTMov.isVisible()) {
                        sent.cargarTablaTipoMov(sent.getG(), sent.getSinFiltro(), tTipoMovVM, despMov, true, true);
                    }

                    break;

                default:
                    JOptionPane.showMessageDialog(null, "Ninguna opcion seleccionada");
                    break;
            }
        } catch (Exception ex) {
            Logger.getLogger(ElementosGraficos.class.getName()).log(Level.SEVERE, null, ex);
            ed.mostrarError("Fallo al gestionar Ficheros", ex);
        }
    }

    /**
     * Metodo para controlar el doble click en una tabla pasada por parametro
     *
     * @param tabla
     */
    private void controlarDobleClickTabla(final JTable tabla) {
        //Controlamos el doble click en la tabla
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    try {
                        gestionarEditarFilaTabla();
                    } catch (Exception ex) {
                        Logger.getLogger(ElementosGraficos.class.getName()).log(Level.SEVERE, null, ex);
                        ed.mostrarError("Fallo al gestionar Doble Click", ex);
                    }
                }
            }
        });
    }

    /**
     * Metodo para controlar el cierre de una ventana pasada por parametro
     *
     * @param frame
     */
    private void cierreVent(final JFrame frame) {
        WindowListener salirVent = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gestionarSalirVent(frame);
            }
        };
        frame.addWindowListener(salirVent);

    }

    /**
     * Metodo que gestiona la salida de las ventanas de la aplicacion
     *
     * @param frame
     */
    private void gestionarSalirVent(JFrame frame) {
        if (frame == this) {
            this.salirVentanaPrincipal();
        } else if (frame == vCtas) {
            this.salirVentanaCtas();
        } else if (frame == vTMov) {
            this.salirVentanaTipoMov();
        } else if (frame == vEditar) {
            this.gestionarCancelarEditarFilaTabla();
        } else if (frame == vAcercaDe) {
            vAcercaDe.dispose();
        }
    }

    /**
     * Metodo que gestiona la eliminaicon de la fila en base a la tabla
     * seleccionada de la aplicacion
     */
    private void gestionarElimnarFilaTabla() {
        if (tMov.getSelectedRowCount() > 0) {
            this.eliminarMov();
        } else if (tTipoMovVM.getSelectedRowCount() > 0) {
            this.eliminarTipoMov();
        } else if (tablaCuentasVC.getSelectedRowCount() > 0) {
            this.eliminarCta();
        } else {
            JOptionPane.showMessageDialog(null, "Ninguna fila seleccionada");
        }
    }

    /**
     * Metodo que gestiona la edicion de la fila en base a la tabla seleccionada
     * de la aplicacion
     */
    private void gestionarEditarFilaTabla() {
        if (tMov.getSelectedRowCount() > 0) {
            this.cargarTablaEditarMov(this.getTipoMov(), tMov);
        } else if (tTipoMovVM.getSelectedRowCount() > 0) {
            this.cargarTablaEditarTipoMov(this.getTipoMov(), tTipoMovVM);
        } else if (tablaCuentasVC.getSelectedRowCount() > 0) {
            this.cargarTablaEditarCta(tablaCuentasVC);
        } else {
            JOptionPane.showMessageDialog(null, "Ninguna fila seleccionada");
        }
    }

    /**
     * Metodo que gestiona el boton aceptar de la tabla editar en base a la
     * tabla seleccionada de la aplicacion
     */
    private void gestionarAceptarEditarFilaTabla() {
        if (tMov.getSelectedRowCount() > 0) {
            this.aceptarEditarMov();
        } else if (tTipoMovVM.getSelectedRowCount() > 0) {
            this.aceptarEditarTipoMov();
        } else if (tablaCuentasVC.getSelectedRowCount() > 0) {
            this.aceptarEditarCta();
        } else {
            JOptionPane.showMessageDialog(null, "Ninguna fila seleccionada");
        }
    }

    /**
     * Metodo que gestiona el boton cancelar de la tabla editar en base a la
     * tabla seleccionada de la aplicacion
     */
    private void gestionarCancelarEditarFilaTabla() {
        if (tMov.getSelectedRowCount() > 0) {
            this.salirVentanaEditarMov();
        } else if (tTipoMovVM.getSelectedRowCount() > 0) {
            this.salirVentanaEditarTipoMov();
        } else if (tablaCuentasVC.getSelectedRowCount() > 0) {
            this.salirVentanaEditarCta();
        }
    }

//------------------------------VENTANA PRINCIPAL---------------------------------//  
    /**
     * Metodo para crear un movimiento en la tabla de la ventana principal
     *
     * @param comboBox1
     * @param comboBox2
     * @param date
     * @param textField
     */
    private void crearMov(JComboBox comboBox1, JComboBox comboBox2, JDateChooser date, JTextField textField) {
        try {
            if (!ed.validarImporte(textField) || !ed.validarFecha(date) || comboBox1.getSelectedItem() == comboBox2.getSelectedItem()) {
                JOptionPane.showMessageDialog(null, "Existen casillas sin rellenar o con datos erroneos");
            } else {
                SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
                String cuenta = (String) comboBox1.getSelectedItem();
                String movimiento = (String) comboBox2.getSelectedItem();
                String importe = (String) textField.getText();
                String fecha = formatoFecha.format((Date) date.getDate());
                boolean capitalSuficiente = false;

                String[] valores = {null, cuenta, movimiento, fecha, importe};
                String tipo = this.getTipoMov();

                int respuesta = 0;
                if (sent.comprobarMovID(tipo, valores)) {
                    respuesta = JOptionPane.showConfirmDialog(null, "Ya has introducido un movimiento exactamente igual\n¿Quieres insertarlo de todas formas?", tipo, JOptionPane.YES_NO_OPTION);
                }

                if (!sent.comprobarMovID(tipo, valores) || respuesta == JOptionPane.YES_OPTION) {
                    capitalSuficiente = sent.insertarMov(tipo, valores, tMov, txtTotal);
                    sent.cargarListaMov(tipo, sent.getSinFiltro(), tMov, txtTotal, true, true);
                    if (vCtas.isVisible()) {
                        this.cargarTablaCtas();
                    }
                    if (!capitalSuficiente) {
                        JOptionPane.showMessageDialog(null, "Atención!!\nSu cuenta ha quedado en números rojos!!", "CUENTA EN NUMEROS ROJOS", JOptionPane.ERROR_MESSAGE);
                    }
                }

                date.setDate(null);
                textField.setText("");
            }
        } catch (Exception ex) {
            Logger.getLogger(ElementosGraficos.class.getName()).log(Level.SEVERE, null, ex);
            ed.mostrarError("Fallo al crear Movimiento", ex);
        }
    }

    /**
     * Metodo para eliminar un movimiento en la tabla de la ventna principal
     */
    private void eliminarMov() {
        try {
            int respuesta = 0;
            respuesta = JOptionPane.showConfirmDialog(null, "¿Seguro que deseas elimnar el movimiento?", "ELIMINAR", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {

                int[] filas = tMov.getSelectedRows();
                if (filas.length > 0) {

                    for (int i = filas.length - 1; i >= 0; i--) {
                        String[] valores = ed.recuperarValoresFila(tMov, filas[i]);
                        sent.eliminarMov(this.getTipoMov(), valores, tMov, txtTotal);
                    }
                    
                    if (vCtas.isVisible()) {
                        this.cargarTablaCtas();
                    }
                }
            }
        } catch (IndexOutOfBoundsException ex){

            System.out.println("mierda!");
            
        }catch (Exception ex) {
            Logger.getLogger(ElementosGraficos.class.getName()).log(Level.SEVERE, null, ex);
            ed.mostrarError("Fallo al eliminar Movimiento", ex);
        }
    }

    /**
     * Metodo que controla el boton aceptar de la ventana editar con datos de la
     * tabla movimientos
     */
    private void aceptarEditarMov() {
        try {
            if (tablaEditar.getSelectedRowCount() > 0) {
                String fecha = ((String) tablaEditar.getValueAt(tablaEditar.getSelectedRow(), 3)).replace("'", "").replace("\\", "");
                String importe = ((String) tablaEditar.getValueAt(tablaEditar.getSelectedRow(), 4)).replace("-", "").replace("'", "").replace("\\", "");;
                boolean capitalSuficiente = false;
                
                if (!ed.comprobarFecha(fecha) || !ed.comprobarFloat(importe, false)) {
                    JOptionPane.showMessageDialog(null, "Existen casillas sin rellenar o con datos erroneos");
                } else {
                    int respuesta = 0;
                    if (ed.compararVectores(ed.recuperarValoresFila(tMov, tMov.getSelectedRow()), ed.recuperarValoresFila(tablaEditar, tablaEditar.getSelectedRow()))) {
                        respuesta = JOptionPane.showConfirmDialog(null, "No se ha modificado ningun elemento\n¿Desea continuar?", "EDITAR", JOptionPane.YES_NO_OPTION);
                    }

                    if (respuesta == JOptionPane.YES_OPTION) {
                        capitalSuficiente = sent.editarMov(this.getTipoMov(), tMov, tablaEditar, txtTotal);
                        if (vCtas.isVisible()) {
                            this.cargarTablaCtas();
                        }
                        this.salirVentanaEditarMov();
                        if(!capitalSuficiente){
                            JOptionPane.showMessageDialog(null, "Atención!!\nSu cuenta ha quedado en números rojos!!", "CUENTA EN NUMEROS ROJOS", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } else {
                this.salirVentanaEditarMov();
            }
        } catch (Exception ex) {
            Logger.getLogger(ElementosGraficos.class.getName()).log(Level.SEVERE, null, ex);
            ed.mostrarError("Fallo al Editar Movimiento", ex);
        }
    }

//------------------------------VENTANA CUENTAS---------------------------------//      
    /**
     * Metodo para crear una cuenta en la tabla de la ventana de mantenimiento
     * de cuentas
     */
    private void crearCta() {

        try {
            if (!ed.validarCampoVacio(txtCuentaVC) || !ed.validarImporte(txtCapitalInicialVC)) {
                JOptionPane.showMessageDialog(null, "Existen casillas sin rellenar o con datos erroneos");
            } else {


                String nombre = txtCuentaVC.getText();
                String capitalIni = txtCapitalInicialVC.getText();
                String numCta = "";

                if (checkNumCuentaVC.isSelected()) {
                    numCta = txtNumCuentaVC1.getText().replace(" ", "") + " "
                            + txtNumCuentaVC2.getText().replace(" ", "") + " "
                            + txtNumCuentaVC3.getText().replace(" ", "") + " "
                            + txtNumCuentaVC4.getText().replace(" ", "");
                }

                if (sent.comprobarCta(sent.getNombreCta(), nombre) && sent.comprobarCta(sent.getNumeroCta(), numCta) && (ed.validarNumeroCta(numCta) || numCta.equals(""))) {
                    String[] valores = {null, nombre, numCta, capitalIni};
                    sent.insertarCta(valores, tablaCuentasVC, despCtas, despTrasOrigen, despTrasDest);

                    txtCuentaVC.setText("");
                    txtCapitalInicialVC.setText("");
                    if (checkNumCuentaVC.isSelected()) {
                        checkNumCuentaVC.setSelected(false);
                        this.estadoNumeroCta(false);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Existen casillas sin rellenar o con datos erroneos\nComprueba que el nombre o número de la cuenta no esten repetidos");
                }

            }
        } catch (Exception ex) {
            Logger.getLogger(ElementosGraficos.class.getName()).log(Level.SEVERE, null, ex);
            ed.mostrarError("Fallo al crear Cuenta", ex);
        }


    }

    /**
     * Metodo para eliminar una cuenta en la tabla de la ventana de
     * mantenimiento de cuentas
     */
    private void eliminarCta() {
        try {
            int respuesta = 0;
            respuesta = JOptionPane.showConfirmDialog(null, "¿Seguro que deseas elimnar la cuenta?", "ELIMINAR", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                respuesta = JOptionPane.showConfirmDialog(null, "Si elimina la cuenta se eliminaran todos los movimientos vinculados a ella\n¿Seguro que realmente deseas elimnar la cuenta?", "ELIMINAR", JOptionPane.YES_NO_OPTION);
                if (respuesta == JOptionPane.YES_OPTION) {
                    sent.eliminarCta(this.getTipoMov(), ed.recuperarValoresFila(tablaCuentasVC, tablaCuentasVC.getSelectedRow()), tablaCuentasVC, despCtas, despTrasOrigen, despTrasDest);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ElementosGraficos.class.getName()).log(Level.SEVERE, null, ex);
            ed.mostrarError("Fallo al eliminar Cuenta", ex);
        }
    }

    /**
     * Metodo que controla el boton aceptar de la ventana editar con datos de la
     * tabla cuentas
     */
    private void aceptarEditarCta() {
        try {
            if (tablaEditar.getSelectedRowCount() > 0) {

                String nombreAntiguo = (String) tablaCuentasVC.getValueAt(tablaCuentasVC.getSelectedRow(), 1);
                String numCtaAntiguo = (String) tablaCuentasVC.getValueAt(tablaCuentasVC.getSelectedRow(), 2);

                String nombreNuevo = ((String) tablaEditar.getValueAt(tablaEditar.getSelectedRow(), 1)).replace("'", "").replace("\\", "");;
                String numCtaNuevo = ((String) tablaEditar.getValueAt(tablaEditar.getSelectedRow(), 2)).replace("'", "").replace("\\", "");;
                String importe = (((String) tablaEditar.getValueAt(tablaEditar.getSelectedRow(), 3))).replace("'", "").replace("\\", "");;

                boolean comprueba = this.comprobarDatosCtaAntesEditar(nombreAntiguo, nombreNuevo, numCtaAntiguo, numCtaNuevo, importe);

                if (comprueba) {
                    sent.editarCta(tablaCuentasVC, tablaEditar, despCtas, despTrasOrigen, despTrasDest);
                    this.salirVentanaEditarCta();
                } else {
                    JOptionPane.showMessageDialog(null, "Existen casillas con datos erroneos");
                }
            } else {
                this.salirVentanaEditarCta();
            }
        } catch (Exception ex) {
            Logger.getLogger(ElementosGraficos.class.getName()).log(Level.SEVERE, null, ex);
            ed.mostrarError("Fallo al Editar Cuenta", ex);
        }
    }

    /**
     * Metodo que comprueba que los datos editados de la cuenta son coherentes
     *
     * @param nombreAntiguo
     * @param nombreNuevo
     * @param numCtaAntiguo
     * @param numCtaNuevo
     * @param importe
     * @return boolean
     * @throws Exception
     */
    private boolean comprobarDatosCtaAntesEditar(String nombreAntiguo, String nombreNuevo, String numCtaAntiguo, String numCtaNuevo, String importe) throws Exception {

        boolean nombre = this.comprobarNombreCta(nombreAntiguo, nombreNuevo);
        boolean numero = this.comprobarNumeroCta(numCtaAntiguo, numCtaNuevo);
        boolean imp = ed.comprobarFloat(importe, true);

        if (nombre && numero && imp) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Metodo que comprueba que el nombre de la cuenta es coherente
     *
     * @param nombreAntiguo
     * @param nombreNuevo
     * @return boolean
     * @throws Exception
     */
    private boolean comprobarNombreCta(String nombreAntiguo, String nombreNuevo) throws Exception {
        boolean nombre = false;
        if (!nombreAntiguo.equals(nombreNuevo)) {
            if (!sent.comprobarCta(sent.getNombreCta(), nombreNuevo)) {
                nombre = false;
            } else {
                nombre = true;
            }
        } else {
            nombre = true;
        }
        return nombre;
    }

    /**
     * Metodo que comprueba que el numero de la cuenta es coherente
     *
     * @param numCtaAntiguo
     * @param numCtaNuevo
     * @return
     * @throws Exception
     */
    private boolean comprobarNumeroCta(String numCtaAntiguo, String numCtaNuevo) throws Exception {
        boolean numero = false;
        if (!numCtaAntiguo.equals(numCtaNuevo)) {
            if (!sent.comprobarCta(sent.getNumeroCta(), numCtaNuevo)) {
                numero = false;
            } else {
                if (!numCtaNuevo.equals("")) {
                    if (!ed.validarNumeroCta(numCtaNuevo)) {
                        numero = false;
                    } else {
                        numero = true;
                    }

                }
            }
        } else {
            numero = true;
        }
        return numero;

    }

    /**
     * Metodo que recarga la tabla cuentas de la ventana de mantenimiento de
     * cuentas y los desplegables correspondientes
     */
    private void cargarTablaCtas() {
        try {
            sent.cargarTablaCuentas(tablaCuentasVC, sent.getSinFiltro(), despCtas, despTrasOrigen, despTrasDest, true, true);
        } catch (Exception ex) {
            Logger.getLogger(ElementosGraficos.class.getName()).log(Level.SEVERE, null, ex);
            ed.mostrarError("Fallo cargar Tabla Cuentas", ex);
        }
        this.mostrarVentanaCtas();

    }

//------------------------------VENTANA MOVIMIENTOS---------------------------------//
    /**
     * Metodo para crear un tipo de movimiento en la tabla de la ventana de
     * mantenimiento de tipos de movimientos
     */
    private void crearTipoMov() {
        try {
            if (!ed.validarCampoVacio(txtTMovVM)) {
                JOptionPane.showMessageDialog(null, "Existen casillas sin rellenar o con datos erroneos");
            } else {

                String nombre = txtTMovVM.getText();
                if (sent.comprobarCta(sent.getNombreCta(), nombre)) {
                    sent.insertarTipoMov(nombre, this.getTipoMov(), tTipoMovVM, despMov);
                    txtTMovVM.setText("");
                } else {
                    JOptionPane.showMessageDialog(null, "El Tipo de Movimiento ya existe");
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ElementosGraficos.class.getName()).log(Level.SEVERE, null, ex);
            ed.mostrarError("Fallo al crear Tipo de Movimiento", ex);
        }
    }

    /**
     * Metodo para eliminar un tipo de movimiento en la tabla de la ventana de
     * mantenimiento de tipos de movimientos
     */
    private void eliminarTipoMov() {
        try {
            int respuesta = 0;
            respuesta = JOptionPane.showConfirmDialog(null, "¿Seguro que deseas elimnar el Tipo de Movimiento?", "ELIMINAR", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                respuesta = JOptionPane.showConfirmDialog(null, "Si elimina el Tipo de Movimiento se eliminaran todos los movimientos vinculados a el\n¿Seguro que realmente deseas elimnarlo?", "ELIMINAR", JOptionPane.YES_NO_OPTION);
                if (respuesta == JOptionPane.YES_OPTION) {
                    sent.eliminarTipoMov(this.getTipoMov(), ed.recuperarValoresFila(tTipoMovVM, tTipoMovVM.getSelectedRow()), tTipoMovVM, despMov);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ElementosGraficos.class.getName()).log(Level.SEVERE, null, ex);
            ed.mostrarError("Fallo al eliminar Tipo de Movimiento", ex);
        }

    }

    /**
     * Metodo que controla el boton aceptar de la ventana editar con datos de la
     * tabla tipo de movimiento
     */
    private void aceptarEditarTipoMov() {
        try {
            if (tablaEditar.getSelectedRowCount() > 0) {
                String nombreAntiguo = (String) tTipoMovVM.getValueAt(tTipoMovVM.getSelectedRow(), 1);
                String nombreNuevo = ((String) tablaEditar.getValueAt(tablaEditar.getSelectedRow(), 1)).replace("'", "").replace("\\", "");;
                boolean comprueba = this.comprobarNombreCta(nombreAntiguo, nombreNuevo);

                if (comprueba) {
                    sent.editarTipoMov(this.getTipoMov(), tTipoMovVM, tablaEditar, despMov);
                    this.salirVentanaEditarTipoMov();
                } else {
                    JOptionPane.showMessageDialog(null, "El Tipo de Movimiento ya existe");
                }

            } else {
                this.salirVentanaEditarTipoMov();
            }
        } catch (Exception ex) {
            Logger.getLogger(ElementosGraficos.class.getName()).log(Level.SEVERE, null, ex);
            ed.mostrarError("Fallo al Editar Tipo de Movimiento", ex);
        }

    }

    /**
     * Metodo que recarga la tabla tipo movimiento de la ventana de
     * mantenimiento de tipo de movimiento y los desplegables correspondientes
     *
     * @param tipo
     * @throws Exception
     */
    private void cargarTablaTipoMov(String tipo) throws Exception {

        sent.cargarTablaTipoMov(tipo, sent.getSinFiltro(), tTipoMovVM, despMov, true, true);

        if (tipo.equals(sent.getG())) {
            vTMov.setTitle(sent.getG().toUpperCase());
            pBajoTMovVM.setBackground(Color.RED);
            pTMovVM.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Gastos", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
            etTMovVM.setText("Nombre Gasto");

        } else if (tipo.equals(sent.getI())) {
            vTMov.setTitle(sent.getI().toUpperCase());
            pBajoTMovVM.setBackground(Color.GREEN);
            pTMovVM.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ingresos", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
            etTMovVM.setText("Nombre Ingreso");
        }
    }
//***********************************FILTROS***********************************//   
//------------------------------VENTANA PRINCIPAL---------------------------------//  

    /**
     * Metodo que reinicializa los field text del panel de buscar
     */
    private void cargarEtFiltroTM() throws Exception {
        DefaultTableModel dtm = (DefaultTableModel) tMov.getModel();
        etFiltroCol1.setText(dtm.getColumnName(1));
        txtFiltroCol1.setText("");
        etFiltroCol2.setText(dtm.getColumnName(2));
        txtFiltroCol2.setText("");
        etFiltroCol3.setText(dtm.getColumnName(3));
        txtFiltroCol3Anyo.setText("");
        etFiltroCol4.setText(dtm.getColumnName(4));
        txtFiltroCol4.setText("");
    }

    /**
     * Metodo que filtra la tabla movimientos de la ventana principal en base a
     * lo solicitado en el panel de buscar
     */
    private void cargarFiltroTM() {
        try {
            sent.cargarFiltroTM(txtFiltroCol1, txtFiltroCol2, txtFiltroCol3Dia, txtFiltroCol3Mes, txtFiltroCol3Anyo, txtFiltroCol4, this.getTipoMov(), tMov, despFiltroCol3, despFiltroCol4, txtTotal);
        } catch (Exception ex) {
            Logger.getLogger(ElementosGraficos.class.getName()).log(Level.SEVERE, null, ex);
            ed.mostrarError("Fallo al gestionar el Filtro", ex);
        }
    }

//***********************************ELEMENTOS AUTOGENERADOS Y ESCUCHADORES***********************************//     
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        vCtas = new javax.swing.JFrame();
        panelGeneralVC = new javax.swing.JPanel();
        panelBajoCuentasVC = new javax.swing.JPanel();
        panelCuentasVC = new javax.swing.JPanel();
        btnEliminarVC = new javax.swing.JButton();
        btnEditarVC = new javax.swing.JButton();
        btnCrearVC = new javax.swing.JButton();
        txtCuentaVC = new javax.swing.JTextField();
        etiqCuentaVC = new javax.swing.JLabel();
        panleBajoTablaCuentasVC = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaCuentasVC = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false; //Disallow the editing of any cell
            }
        };
        btnSalirVC = new javax.swing.JButton();
        checkNumCuentaVC = new javax.swing.JCheckBox();
        etiqCapitalInicialVC = new javax.swing.JLabel();
        txtCapitalInicialVC = new javax.swing.JTextField();
        etiqEurVC = new javax.swing.JLabel();
        panelCuentasVCNumCta = new javax.swing.JPanel();
        txtNumCuentaVC1 = new javax.swing.JTextField();
        txtNumCuentaVC2 = new javax.swing.JTextField();
        txtNumCuentaVC3 = new javax.swing.JTextField();
        txtNumCuentaVC4 = new javax.swing.JTextField();
        btnNuevaVC = new javax.swing.JButton();
        vTMov = new javax.swing.JFrame();
        pGralVM = new javax.swing.JPanel();
        pBajoTMovVM = new javax.swing.JPanel();
        pTMovVM = new javax.swing.JPanel();
        btnEliminarVM = new javax.swing.JButton();
        btnEditarVM = new javax.swing.JButton();
        btnCrearVM = new javax.swing.JButton();
        txtTMovVM = new javax.swing.JTextField();
        etTMovVM = new javax.swing.JLabel();
        pBajoTTMovVM = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tTipoMovVM = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false; //Disallow the editing of any cell
            }
        };
        btnSalirVM = new javax.swing.JButton();
        btnNuevoVM = new javax.swing.JButton();
        jPpMenuTablas = new javax.swing.JPopupMenu();
        subMenuEmEditar = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        subMenuEmEliminar = new javax.swing.JMenuItem();
        vEditar = new javax.swing.JFrame();
        panelBajoEditarVE = new javax.swing.JPanel();
        panelEditarVE = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        tablaEditar = new javax.swing.JTable();
        btnCancelarVE = new javax.swing.JButton();
        btnAceptarVE = new javax.swing.JButton();
        vAcercaDe = new javax.swing.JFrame();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnJavaDoc = new javax.swing.JButton();
        btnGitHub = new javax.swing.JButton();
        btnDocum = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        btnSalirVA = new javax.swing.JButton();
        pGral = new javax.swing.JPanel();
        pBajoMov = new javax.swing.JPanel();
        pMov = new javax.swing.JPanel();
        despMov = new javax.swing.JComboBox();
        etTipoMov = new javax.swing.JLabel();
        btnTipoMov = new javax.swing.JButton();
        etImpMov = new javax.swing.JLabel();
        etFechaMov = new javax.swing.JLabel();
        txtImpMov = new javax.swing.JTextField();
        etEur = new javax.swing.JLabel();
        txtFechaMov = new com.toedter.calendar.JDateChooser();
        btnAceptarMov = new javax.swing.JButton();
        btnCtas = new javax.swing.JButton();
        etCtas = new javax.swing.JLabel();
        despCtas = new javax.swing.JComboBox();
        pBajoTM = new javax.swing.JPanel();
        pTM = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tMov = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false; //Disallow the editing of any cell
            }
        };
        btnEliminarTM = new javax.swing.JButton();
        btnGraficaTM = new javax.swing.JButton();
        btnEditarTM = new javax.swing.JButton();
        pFiltro = new javax.swing.JPanel();
        txtFiltroCol1 = new javax.swing.JTextField();
        txtFiltroCol2 = new javax.swing.JTextField();
        txtFiltroCol3Anyo = new javax.swing.JTextField();
        txtFiltroCol4 = new javax.swing.JTextField();
        etFiltroCol1 = new javax.swing.JLabel();
        etFiltroCol2 = new javax.swing.JLabel();
        etFiltroCol3 = new javax.swing.JLabel();
        etFiltroCol4 = new javax.swing.JLabel();
        despFiltroCol4 = new javax.swing.JComboBox();
        despFiltroCol3 = new javax.swing.JComboBox();
        txtFiltroCol3Dia = new javax.swing.JTextField();
        txtFiltroCol3Mes = new javax.swing.JTextField();
        btn2pFiltroTM = new javax.swing.JToggleButton();
        btnImprimirTM = new javax.swing.JButton();
        txtTotal = new javax.swing.JTextField();
        etTotal = new javax.swing.JLabel();
        barraHerramientas = new javax.swing.JToolBar();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btn2pG = new javax.swing.JToggleButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        btn2pT = new javax.swing.JToggleButton();
        jSeparator13 = new javax.swing.JToolBar.Separator();
        btn2pI = new javax.swing.JToggleButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btn2pTM = new javax.swing.JToggleButton();
        pBajoTras = new javax.swing.JPanel();
        pTras = new javax.swing.JPanel();
        despTrasOrigen = new javax.swing.JComboBox();
        etTrasOrigen = new javax.swing.JLabel();
        etImpTras = new javax.swing.JLabel();
        etFechaTras = new javax.swing.JLabel();
        txtImpTras = new javax.swing.JTextField();
        etEur3 = new javax.swing.JLabel();
        txtFechaTras = new com.toedter.calendar.JDateChooser();
        btnAceptarTras = new javax.swing.JButton();
        despTrasDest = new javax.swing.JComboBox();
        etTrasDest = new javax.swing.JLabel();
        barraMenu = new javax.swing.JMenuBar();
        mArchivo = new javax.swing.JMenu();
        smAbrirArchivo = new javax.swing.JMenuItem();
        smGuardarArchivo = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        smG = new javax.swing.JMenuItem();
        smT = new javax.swing.JMenuItem();
        smI = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        smCambiaCta = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        smSalir = new javax.swing.JMenuItem();
        mEdicion = new javax.swing.JMenu();
        smAddCuenta = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        smAddTG = new javax.swing.JMenuItem();
        smAddTI = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        smFiltro = new javax.swing.JMenuItem();
        mAyuda = new javax.swing.JMenu();
        subMenuAcercaDe = new javax.swing.JMenuItem();
        subMenuInstrucc = new javax.swing.JMenuItem();

        vCtas.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        vCtas.setTitle("CUENTAS");

        panelBajoCuentasVC.setBackground(new java.awt.Color(128, 128, 128));

        panelCuentasVC.setBorder(javax.swing.BorderFactory.createTitledBorder("Cuentas"));

        btnEliminarVC.setText("Eliminar");
        btnEliminarVC.setEnabled(false);
        btnEliminarVC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarVCActionPerformed(evt);
            }
        });

        btnEditarVC.setText("Editar");
        btnEditarVC.setEnabled(false);
        btnEditarVC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarVCActionPerformed(evt);
            }
        });

        btnCrearVC.setText("Crear");
        btnCrearVC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCrearVCActionPerformed(evt);
            }
        });

        etiqCuentaVC.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        etiqCuentaVC.setText("Nombre Cuenta");

        panleBajoTablaCuentasVC.setBackground(new java.awt.Color(192, 192, 192));

        tablaCuentasVC.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaCuentasVC.getTableHeader().setReorderingAllowed(false);
        tablaCuentasVC.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tablaCuentasVCMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(tablaCuentasVC);

        javax.swing.GroupLayout panleBajoTablaCuentasVCLayout = new javax.swing.GroupLayout(panleBajoTablaCuentasVC);
        panleBajoTablaCuentasVC.setLayout(panleBajoTablaCuentasVCLayout);
        panleBajoTablaCuentasVCLayout.setHorizontalGroup(
            panleBajoTablaCuentasVCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panleBajoTablaCuentasVCLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        panleBajoTablaCuentasVCLayout.setVerticalGroup(
            panleBajoTablaCuentasVCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panleBajoTablaCuentasVCLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnSalirVC.setText("Salir");
        btnSalirVC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirVCActionPerformed(evt);
            }
        });

        checkNumCuentaVC.setText("Numero Cuenta (Opcional)");
        checkNumCuentaVC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkNumCuentaVCActionPerformed(evt);
            }
        });

        etiqCapitalInicialVC.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        etiqCapitalInicialVC.setText("Capital Inicial");

        txtCapitalInicialVC.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCapitalInicialVCKeyReleased(evt);
            }
        });

        etiqEurVC.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        etiqEurVC.setText("Eur");

        txtNumCuentaVC1.setToolTipText("");
        txtNumCuentaVC1.setEnabled(false);
        txtNumCuentaVC1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNumCuentaVC1KeyReleased(evt);
            }
        });

        txtNumCuentaVC2.setEnabled(false);
        txtNumCuentaVC2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNumCuentaVC2KeyReleased(evt);
            }
        });

        txtNumCuentaVC3.setEnabled(false);
        txtNumCuentaVC3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNumCuentaVC3KeyReleased(evt);
            }
        });

        txtNumCuentaVC4.setEnabled(false);
        txtNumCuentaVC4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNumCuentaVC4KeyReleased(evt);
            }
        });

        javax.swing.GroupLayout panelCuentasVCNumCtaLayout = new javax.swing.GroupLayout(panelCuentasVCNumCta);
        panelCuentasVCNumCta.setLayout(panelCuentasVCNumCtaLayout);
        panelCuentasVCNumCtaLayout.setHorizontalGroup(
            panelCuentasVCNumCtaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCuentasVCNumCtaLayout.createSequentialGroup()
                .addComponent(txtNumCuentaVC1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtNumCuentaVC2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtNumCuentaVC3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtNumCuentaVC4))
        );
        panelCuentasVCNumCtaLayout.setVerticalGroup(
            panelCuentasVCNumCtaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCuentasVCNumCtaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txtNumCuentaVC1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtNumCuentaVC2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtNumCuentaVC3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtNumCuentaVC4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        btnNuevaVC.setText("Nueva");
        btnNuevaVC.setEnabled(false);
        btnNuevaVC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevaVCActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelCuentasVCLayout = new javax.swing.GroupLayout(panelCuentasVC);
        panelCuentasVC.setLayout(panelCuentasVCLayout);
        panelCuentasVCLayout.setHorizontalGroup(
            panelCuentasVCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panleBajoTablaCuentasVC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panelCuentasVCLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCuentasVCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCuentasVCLayout.createSequentialGroup()
                        .addComponent(etiqCuentaVC)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCuentaVC, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(etiqCapitalInicialVC)
                        .addGap(6, 6, 6)
                        .addComponent(txtCapitalInicialVC, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(etiqEurVC)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCrearVC, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelCuentasVCLayout.createSequentialGroup()
                        .addComponent(checkNumCuentaVC)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelCuentasVCNumCta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelCuentasVCLayout.createSequentialGroup()
                        .addComponent(btnEditarVC, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEliminarVC)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNuevaVC, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSalirVC, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelCuentasVCLayout.setVerticalGroup(
            panelCuentasVCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelCuentasVCLayout.createSequentialGroup()
                .addGroup(panelCuentasVCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCuentaVC, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etiqCuentaVC, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCrearVC)
                    .addComponent(etiqCapitalInicialVC, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCapitalInicialVC, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etiqEurVC, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCuentasVCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkNumCuentaVC)
                    .addComponent(panelCuentasVCNumCta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panleBajoTablaCuentasVC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCuentasVCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEditarVC, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNuevaVC, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminarVC, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSalirVC, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout panelBajoCuentasVCLayout = new javax.swing.GroupLayout(panelBajoCuentasVC);
        panelBajoCuentasVC.setLayout(panelBajoCuentasVCLayout);
        panelBajoCuentasVCLayout.setHorizontalGroup(
            panelBajoCuentasVCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBajoCuentasVCLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCuentasVC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelBajoCuentasVCLayout.setVerticalGroup(
            panelBajoCuentasVCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBajoCuentasVCLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCuentasVC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout panelGeneralVCLayout = new javax.swing.GroupLayout(panelGeneralVC);
        panelGeneralVC.setLayout(panelGeneralVCLayout);
        panelGeneralVCLayout.setHorizontalGroup(
            panelGeneralVCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelGeneralVCLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelBajoCuentasVC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelGeneralVCLayout.setVerticalGroup(
            panelGeneralVCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralVCLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelBajoCuentasVC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout vCtasLayout = new javax.swing.GroupLayout(vCtas.getContentPane());
        vCtas.getContentPane().setLayout(vCtasLayout);
        vCtasLayout.setHorizontalGroup(
            vCtasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelGeneralVC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        vCtasLayout.setVerticalGroup(
            vCtasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelGeneralVC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        vTMov.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        vTMov.setTitle("GASTOS");

        pBajoTMovVM.setBackground(new java.awt.Color(255, 0, 0));

        pTMovVM.setBorder(javax.swing.BorderFactory.createTitledBorder("Gastos"));

        btnEliminarVM.setText("Eliminar");
        btnEliminarVM.setEnabled(false);
        btnEliminarVM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarVMActionPerformed(evt);
            }
        });

        btnEditarVM.setText("Editar");
        btnEditarVM.setEnabled(false);
        btnEditarVM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarVMActionPerformed(evt);
            }
        });

        btnCrearVM.setText("Crear");
        btnCrearVM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCrearVMActionPerformed(evt);
            }
        });

        etTMovVM.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        etTMovVM.setText("Nombre Gasto");

        pBajoTTMovVM.setBackground(new java.awt.Color(192, 192, 192));

        tTipoMovVM.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tTipoMovVM.getTableHeader().setReorderingAllowed(false);
        tTipoMovVM.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tTipoMovVMMousePressed(evt);
            }
        });
        jScrollPane5.setViewportView(tTipoMovVM);

        javax.swing.GroupLayout pBajoTTMovVMLayout = new javax.swing.GroupLayout(pBajoTTMovVM);
        pBajoTTMovVM.setLayout(pBajoTTMovVMLayout);
        pBajoTTMovVMLayout.setHorizontalGroup(
            pBajoTTMovVMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pBajoTTMovVMLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5)
                .addContainerGap())
        );
        pBajoTTMovVMLayout.setVerticalGroup(
            pBajoTTMovVMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pBajoTTMovVMLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnSalirVM.setText("Salir");
        btnSalirVM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirVMActionPerformed(evt);
            }
        });

        btnNuevoVM.setText("Nuevo");
        btnNuevoVM.setEnabled(false);
        btnNuevoVM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoVMActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pTMovVMLayout = new javax.swing.GroupLayout(pTMovVM);
        pTMovVM.setLayout(pTMovVMLayout);
        pTMovVMLayout.setHorizontalGroup(
            pTMovVMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pBajoTTMovVM, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pTMovVMLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pTMovVMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pTMovVMLayout.createSequentialGroup()
                        .addComponent(btnEditarVM, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEliminarVM)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNuevoVM, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 170, Short.MAX_VALUE)
                        .addComponent(btnSalirVM, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pTMovVMLayout.createSequentialGroup()
                        .addComponent(etTMovVM)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTMovVM, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCrearVM, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pTMovVMLayout.setVerticalGroup(
            pTMovVMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pTMovVMLayout.createSequentialGroup()
                .addGroup(pTMovVMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCrearVM)
                    .addComponent(txtTMovVM, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etTMovVM, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pBajoTTMovVM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pTMovVMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNuevoVM, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminarVM, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditarVM, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSalirVM, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout pBajoTMovVMLayout = new javax.swing.GroupLayout(pBajoTMovVM);
        pBajoTMovVM.setLayout(pBajoTMovVMLayout);
        pBajoTMovVMLayout.setHorizontalGroup(
            pBajoTMovVMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pBajoTMovVMLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pTMovVM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pBajoTMovVMLayout.setVerticalGroup(
            pBajoTMovVMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pBajoTMovVMLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pTMovVM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout pGralVMLayout = new javax.swing.GroupLayout(pGralVM);
        pGralVM.setLayout(pGralVMLayout);
        pGralVMLayout.setHorizontalGroup(
            pGralVMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pGralVMLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pBajoTMovVM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pGralVMLayout.setVerticalGroup(
            pGralVMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pGralVMLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pBajoTMovVM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout vTMovLayout = new javax.swing.GroupLayout(vTMov.getContentPane());
        vTMov.getContentPane().setLayout(vTMovLayout);
        vTMovLayout.setHorizontalGroup(
            vTMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pGralVM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        vTMovLayout.setVerticalGroup(
            vTMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pGralVM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        subMenuEmEditar.setText("Editar");
        subMenuEmEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuEmEditarActionPerformed(evt);
            }
        });
        jPpMenuTablas.add(subMenuEmEditar);
        jPpMenuTablas.add(jSeparator9);

        subMenuEmEliminar.setText("Eliminar");
        subMenuEmEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuEmEliminarActionPerformed(evt);
            }
        });
        jPpMenuTablas.add(subMenuEmEliminar);

        vEditar.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        vEditar.setTitle("EDITAR");
        vEditar.setResizable(false);

        panelBajoEditarVE.setBackground(new java.awt.Color(128, 128, 128));

        panelEditarVE.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Editar", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        tablaEditar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaEditar.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(tablaEditar);

        btnCancelarVE.setText("Cancelar");
        btnCancelarVE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarVEActionPerformed(evt);
            }
        });

        btnAceptarVE.setText("Aceptar");
        btnAceptarVE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarVEActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelEditarVELayout = new javax.swing.GroupLayout(panelEditarVE);
        panelEditarVE.setLayout(panelEditarVELayout);
        panelEditarVELayout.setHorizontalGroup(
            panelEditarVELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEditarVELayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAceptarVE)
                .addGap(18, 18, 18)
                .addComponent(btnCancelarVE)
                .addContainerGap())
        );
        panelEditarVELayout.setVerticalGroup(
            panelEditarVELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEditarVELayout.createSequentialGroup()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEditarVELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelarVE, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAceptarVE, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout panelBajoEditarVELayout = new javax.swing.GroupLayout(panelBajoEditarVE);
        panelBajoEditarVE.setLayout(panelBajoEditarVELayout);
        panelBajoEditarVELayout.setHorizontalGroup(
            panelBajoEditarVELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBajoEditarVELayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelEditarVE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelBajoEditarVELayout.setVerticalGroup(
            panelBajoEditarVELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBajoEditarVELayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelEditarVE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout vEditarLayout = new javax.swing.GroupLayout(vEditar.getContentPane());
        vEditar.getContentPane().setLayout(vEditarLayout);
        vEditarLayout.setHorizontalGroup(
            vEditarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vEditarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelBajoEditarVE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        vEditarLayout.setVerticalGroup(
            vEditarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vEditarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelBajoEditarVE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        vAcercaDe.setTitle("ACERCA DE...");

        jLabel1.setText("<html>\n <body>\n <h2 align=\"center\"><u>CUENTA HOGAR</u></h2>  \n  <p>Cuenta Hogar es una aplicación realizada como proyecto final del Curso de Formación  Profesional de Técnico Superior en Desarrollo de Aplicaciones Multiplataforma, impartido en el Instituto Francesc de Borja Moll.  La utilidad que pretende dicha aplicación es el control de la economía doméstica</p>\n <br/>\n <h3>INFORMACION ADICIONAL</h3>  \n  <p>\"Cuenta Hogar\" ha sido realizado por:\n  <br/>\n  Alumno: Fernando J. González López \n  <br/> \n  Email: <a href=\"mailto:dam1afjgonzalez@gmail.com\">dam1afjgonzalez@gmail.com</a></p>  \n <br/>\n <h3>I.E.S BORJA MOLL</h3>  \n  <p>Tel: 971278150 \n  <br/> Pagina Web: <a href=\"http://www.iesfbmoll.com/\">http://www.iesfbmoll.com/</a>\n  <br/> Direccion: C/ Caracas 6  (Poligono de Levante), Palma de Mallorca (Illes Balears)</p>\n </body> \n</html >");

        btnJavaDoc.setText("JavaDoc");
        btnJavaDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJavaDocActionPerformed(evt);
            }
        });

        btnGitHub.setText("GitHub");
        btnGitHub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGitHubActionPerformed(evt);
            }
        });

        btnDocum.setText("Documentación");
        btnDocum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDocumActionPerformed(evt);
            }
        });

        btnSalirVA.setText("Salir");
        btnSalirVA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirVAActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jSeparator4))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(btnJavaDoc)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGitHub)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDocum)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                .addComponent(btnSalirVA)
                .addGap(20, 20, 20))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnJavaDoc)
                    .addComponent(btnGitHub)
                    .addComponent(btnDocum)
                    .addComponent(btnSalirVA))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout vAcercaDeLayout = new javax.swing.GroupLayout(vAcercaDe.getContentPane());
        vAcercaDe.getContentPane().setLayout(vAcercaDeLayout);
        vAcercaDeLayout.setHorizontalGroup(
            vAcercaDeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        vAcercaDeLayout.setVerticalGroup(
            vAcercaDeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("CuentaHogar");

        pBajoMov.setBackground(new java.awt.Color(192, 192, 192));
        pBajoMov.setEnabled(false);

        pMov.setBorder(javax.swing.BorderFactory.createTitledBorder("Gastos"));
        pMov.setEnabled(false);

        despMov.setToolTipText("Seleccionar Gastos");
        despMov.setEnabled(false);

        etTipoMov.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        etTipoMov.setText("Gasto");
        etTipoMov.setEnabled(false);

        btnTipoMov.setText("...");
        btnTipoMov.setToolTipText("Añadir Tipo Gasto...");
        btnTipoMov.setEnabled(false);
        btnTipoMov.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTipoMovActionPerformed(evt);
            }
        });

        etImpMov.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        etImpMov.setText("Importe");
        etImpMov.setEnabled(false);

        etFechaMov.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        etFechaMov.setText("Fecha");
        etFechaMov.setEnabled(false);

        txtImpMov.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtImpMov.setToolTipText("nnnn,nn");
        txtImpMov.setEnabled(false);
        txtImpMov.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtImpMovKeyReleased(evt);
            }
        });

        etEur.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        etEur.setText("Eur");
        etEur.setEnabled(false);

        txtFechaMov.setToolTipText("dd-MM-yyyy");
        txtFechaMov.setDateFormatString("dd-MM-yyyy");
        txtFechaMov.setEnabled(false);

        btnAceptarMov.setText("Aceptar");
        btnAceptarMov.setToolTipText("Insertar Datos en Tabla");
        btnAceptarMov.setEnabled(false);
        btnAceptarMov.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarMovActionPerformed(evt);
            }
        });

        btnCtas.setText("...");
        btnCtas.setToolTipText("Añadir Cuenta...");
        btnCtas.setEnabled(false);
        btnCtas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCtasActionPerformed(evt);
            }
        });

        etCtas.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        etCtas.setText("Cuenta");
        etCtas.setEnabled(false);

        despCtas.setToolTipText("Seleccionar Cuenta");
        despCtas.setEnabled(false);

        javax.swing.GroupLayout pMovLayout = new javax.swing.GroupLayout(pMov);
        pMov.setLayout(pMovLayout);
        pMovLayout.setHorizontalGroup(
            pMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pMovLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pMovLayout.createSequentialGroup()
                        .addGroup(pMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(etFechaMov)
                            .addComponent(etImpMov))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pMovLayout.createSequentialGroup()
                                .addComponent(txtFechaMov, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAceptarMov)
                                .addContainerGap())
                            .addGroup(pMovLayout.createSequentialGroup()
                                .addComponent(txtImpMov)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(etEur, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(17, 17, 17))))
                    .addGroup(pMovLayout.createSequentialGroup()
                        .addGroup(pMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(etCtas)
                            .addComponent(etTipoMov))
                        .addGap(17, 17, 17)
                        .addGroup(pMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(despMov, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(despCtas, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnCtas)
                            .addComponent(btnTipoMov))
                        .addContainerGap())))
        );
        pMovLayout.setVerticalGroup(
            pMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pMovLayout.createSequentialGroup()
                .addGroup(pMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(despCtas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etCtas, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCtas))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(despMov, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etTipoMov, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTipoMov))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtImpMov, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etImpMov, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etEur, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnAceptarMov, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtFechaMov, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(etFechaMov, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pBajoMovLayout = new javax.swing.GroupLayout(pBajoMov);
        pBajoMov.setLayout(pBajoMovLayout);
        pBajoMovLayout.setHorizontalGroup(
            pBajoMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pBajoMovLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pMov, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pBajoMovLayout.setVerticalGroup(
            pBajoMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pBajoMovLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pMov, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pBajoTM.setBackground(new java.awt.Color(192, 192, 192));
        pBajoTM.setEnabled(false);

        pTM.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Listado de Gastos", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pTM.setEnabled(false);

        tMov.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tMov.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tMov.getTableHeader().setReorderingAllowed(false);
        tMov.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tMovMousePressed(evt);
            }
        });
        jScrollPane3.setViewportView(tMov);

        btnEliminarTM.setText("Eliminar");
        btnEliminarTM.setEnabled(false);
        btnEliminarTM.setFocusable(false);
        btnEliminarTM.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEliminarTM.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEliminarTM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarTMActionPerformed(evt);
            }
        });

        btnGraficaTM.setText("Gráfica");
        btnGraficaTM.setToolTipText("");
        btnGraficaTM.setEnabled(false);
        btnGraficaTM.setFocusable(false);
        btnGraficaTM.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGraficaTM.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGraficaTM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGraficaTMActionPerformed(evt);
            }
        });

        btnEditarTM.setText("Editar");
        btnEditarTM.setEnabled(false);
        btnEditarTM.setFocusable(false);
        btnEditarTM.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEditarTM.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEditarTM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarTMActionPerformed(evt);
            }
        });

        pFiltro.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Buscar", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pFiltro.setEnabled(false);

        txtFiltroCol1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFiltroCol1.setToolTipText("Cuenta");
        txtFiltroCol1.setEnabled(false);
        txtFiltroCol1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFiltroCol1KeyReleased(evt);
            }
        });

        txtFiltroCol2.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFiltroCol2.setToolTipText("Movimiento");
        txtFiltroCol2.setEnabled(false);
        txtFiltroCol2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFiltroCol2KeyReleased(evt);
            }
        });

        txtFiltroCol3Anyo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFiltroCol3Anyo.setToolTipText("Año");
        txtFiltroCol3Anyo.setEnabled(false);
        txtFiltroCol3Anyo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFiltroCol3AnyoKeyReleased(evt);
            }
        });

        txtFiltroCol4.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFiltroCol4.setToolTipText("Importe");
        txtFiltroCol4.setEnabled(false);
        txtFiltroCol4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFiltroCol4KeyReleased(evt);
            }
        });

        etFiltroCol1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        etFiltroCol1.setText("etFiltroCol0");
        etFiltroCol1.setEnabled(false);

        etFiltroCol2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        etFiltroCol2.setText("etFiltroCol1");
        etFiltroCol2.setEnabled(false);

        etFiltroCol3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        etFiltroCol3.setText("etFiltroCol2");
        etFiltroCol3.setEnabled(false);

        etFiltroCol4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        etFiltroCol4.setText("etFiltroCol3");
        etFiltroCol4.setEnabled(false);

        despFiltroCol4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", ">=", "<=" }));
        despFiltroCol4.setEnabled(false);
        despFiltroCol4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                despFiltroCol4ActionPerformed(evt);
            }
        });

        despFiltroCol3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", ">=", "<=" }));
        despFiltroCol3.setEnabled(false);
        despFiltroCol3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                despFiltroCol3ActionPerformed(evt);
            }
        });

        txtFiltroCol3Dia.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFiltroCol3Dia.setToolTipText("Dia");
        txtFiltroCol3Dia.setEnabled(false);
        txtFiltroCol3Dia.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFiltroCol3DiaKeyReleased(evt);
            }
        });

        txtFiltroCol3Mes.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFiltroCol3Mes.setToolTipText("Mes");
        txtFiltroCol3Mes.setEnabled(false);
        txtFiltroCol3Mes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFiltroCol3MesKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout pFiltroLayout = new javax.swing.GroupLayout(pFiltro);
        pFiltro.setLayout(pFiltroLayout);
        pFiltroLayout.setHorizontalGroup(
            pFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pFiltroLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtFiltroCol1)
                    .addComponent(etFiltroCol1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtFiltroCol2)
                    .addComponent(etFiltroCol2, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pFiltroLayout.createSequentialGroup()
                        .addComponent(txtFiltroCol3Dia, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFiltroCol3Mes, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(etFiltroCol3, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(despFiltroCol3, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFiltroCol3Anyo, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pFiltroLayout.createSequentialGroup()
                        .addComponent(etFiltroCol4, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(despFiltroCol4, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(txtFiltroCol4, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pFiltroLayout.setVerticalGroup(
            pFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pFiltroLayout.createSequentialGroup()
                .addGroup(pFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(etFiltroCol2)
                        .addComponent(etFiltroCol1, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(despFiltroCol4, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(etFiltroCol4, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(despFiltroCol3, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(etFiltroCol3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFiltroCol4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtFiltroCol1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtFiltroCol2)
                        .addComponent(txtFiltroCol3Dia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtFiltroCol3Mes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtFiltroCol3Anyo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        btn2pFiltroTM.setText("Buscar");
        btn2pFiltroTM.setEnabled(false);
        btn2pFiltroTM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn2pFiltroTMActionPerformed(evt);
            }
        });

        btnImprimirTM.setText("Imprimir");
        btnImprimirTM.setEnabled(false);
        btnImprimirTM.setFocusable(false);
        btnImprimirTM.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnImprimirTM.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImprimirTM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirTMActionPerformed(evt);
            }
        });

        txtTotal.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotal.setEnabled(false);

        etTotal.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        etTotal.setLabelFor(etCtas);
        etTotal.setText("Total");
        etTotal.setToolTipText("");

        javax.swing.GroupLayout pTMLayout = new javax.swing.GroupLayout(pTM);
        pTM.setLayout(pTMLayout);
        pTMLayout.setHorizontalGroup(
            pTMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
            .addGroup(pTMLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pTMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pTMLayout.createSequentialGroup()
                        .addComponent(pFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pTMLayout.createSequentialGroup()
                        .addComponent(btnEditarTM)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEliminarTM)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnImprimirTM)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGraficaTM))
                    .addGroup(pTMLayout.createSequentialGroup()
                        .addComponent(btn2pFiltroTM)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(etTotal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pTMLayout.setVerticalGroup(
            pTMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pTMLayout.createSequentialGroup()
                .addGroup(pTMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGraficaTM, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnImprimirTM, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditarTM, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminarTM, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pTMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etTotal)
                    .addComponent(btn2pFiltroTM, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout pBajoTMLayout = new javax.swing.GroupLayout(pBajoTM);
        pBajoTM.setLayout(pBajoTMLayout);
        pBajoTMLayout.setHorizontalGroup(
            pBajoTMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pBajoTMLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pTM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pBajoTMLayout.setVerticalGroup(
            pBajoTMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pBajoTMLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pTM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        barraHerramientas.setRollover(true);
        barraHerramientas.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        barraHerramientas.setEnabled(false);

        jSeparator3.setEnabled(false);
        barraHerramientas.add(jSeparator3);

        btn2pG.setText("Gastos");
        btn2pG.setEnabled(false);
        btn2pG.setFocusable(false);
        btn2pG.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn2pG.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btn2pG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn2pGActionPerformed(evt);
            }
        });
        barraHerramientas.add(btn2pG);

        jSeparator6.setEnabled(false);
        barraHerramientas.add(jSeparator6);

        btn2pT.setText("Traspasos");
        btn2pT.setEnabled(false);
        btn2pT.setFocusable(false);
        btn2pT.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn2pT.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btn2pT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn2pTActionPerformed(evt);
            }
        });
        barraHerramientas.add(btn2pT);

        jSeparator13.setEnabled(false);
        barraHerramientas.add(jSeparator13);

        btn2pI.setText("Ingresos");
        btn2pI.setEnabled(false);
        btn2pI.setFocusable(false);
        btn2pI.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn2pI.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btn2pI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn2pIActionPerformed(evt);
            }
        });
        barraHerramientas.add(btn2pI);

        jSeparator2.setEnabled(false);
        barraHerramientas.add(jSeparator2);

        btn2pTM.setEnabled(false);
        btn2pTM.setFocusable(false);
        btn2pTM.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn2pTM.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barraHerramientas.add(btn2pTM);

        pBajoTras.setBackground(new java.awt.Color(192, 192, 192));
        pBajoTras.setEnabled(false);

        pTras.setBorder(javax.swing.BorderFactory.createTitledBorder("Traspasos"));
        pTras.setToolTipText("");
        pTras.setEnabled(false);

        despTrasOrigen.setBackground(new java.awt.Color(255, 0, 0));
        despTrasOrigen.setToolTipText("Seleccionar Gastos");
        despTrasOrigen.setEnabled(false);

        etTrasOrigen.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        etTrasOrigen.setText("Cuenta origen");
        etTrasOrigen.setEnabled(false);

        etImpTras.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        etImpTras.setText("Importe");
        etImpTras.setEnabled(false);

        etFechaTras.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        etFechaTras.setText("Fecha");
        etFechaTras.setEnabled(false);

        txtImpTras.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtImpTras.setToolTipText("nnnn,nn");
        txtImpTras.setEnabled(false);
        txtImpTras.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtImpTrasKeyReleased(evt);
            }
        });

        etEur3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        etEur3.setText("Eur");
        etEur3.setEnabled(false);

        txtFechaTras.setToolTipText("dd-MM-yyyy");
        txtFechaTras.setDateFormatString("dd-MM-yyyy");
        txtFechaTras.setEnabled(false);

        btnAceptarTras.setText("Aceptar");
        btnAceptarTras.setToolTipText("Insertar Datos en Tabla");
        btnAceptarTras.setEnabled(false);
        btnAceptarTras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarTrasActionPerformed(evt);
            }
        });

        despTrasDest.setBackground(new java.awt.Color(0, 255, 0));
        despTrasDest.setToolTipText("Seleccionar Gastos");
        despTrasDest.setEnabled(false);

        etTrasDest.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        etTrasDest.setText("Cuenta destino");
        etTrasDest.setEnabled(false);

        javax.swing.GroupLayout pTrasLayout = new javax.swing.GroupLayout(pTras);
        pTras.setLayout(pTrasLayout);
        pTrasLayout.setHorizontalGroup(
            pTrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pTrasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pTrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pTrasLayout.createSequentialGroup()
                        .addGroup(pTrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(etTrasDest)
                            .addComponent(etTrasOrigen))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pTrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(despTrasOrigen, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(despTrasDest, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(pTrasLayout.createSequentialGroup()
                        .addGroup(pTrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(etFechaTras)
                            .addComponent(etImpTras))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pTrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pTrasLayout.createSequentialGroup()
                                .addComponent(txtImpTras, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(etEur3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 14, Short.MAX_VALUE))
                            .addGroup(pTrasLayout.createSequentialGroup()
                                .addComponent(txtFechaTras, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAceptarTras)))))
                .addContainerGap())
        );
        pTrasLayout.setVerticalGroup(
            pTrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pTrasLayout.createSequentialGroup()
                .addGroup(pTrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(despTrasOrigen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etTrasOrigen, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pTrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(despTrasDest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etTrasDest, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pTrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtImpTras, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etImpTras, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etEur3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pTrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnAceptarTras, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtFechaTras, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(etFechaTras, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pBajoTrasLayout = new javax.swing.GroupLayout(pBajoTras);
        pBajoTras.setLayout(pBajoTrasLayout);
        pBajoTrasLayout.setHorizontalGroup(
            pBajoTrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pBajoTrasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pTras, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pBajoTrasLayout.setVerticalGroup(
            pBajoTrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pBajoTrasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pTras, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout pGralLayout = new javax.swing.GroupLayout(pGral);
        pGral.setLayout(pGralLayout);
        pGralLayout.setHorizontalGroup(
            pGralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(barraHerramientas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pGralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pGralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pBajoMov, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pBajoTras, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pBajoTM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pGralLayout.setVerticalGroup(
            pGralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pGralLayout.createSequentialGroup()
                .addComponent(barraHerramientas, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pGralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pGralLayout.createSequentialGroup()
                        .addComponent(pBajoMov, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pBajoTras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(pBajoTM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        barraMenu.setEnabled(false);

        mArchivo.setText("Archivo");
        mArchivo.setEnabled(false);

        smAbrirArchivo.setIcon(new javax.swing.ImageIcon("/home/fernando/NetBeansProjects/CuentaHogar_6/iconos/iconoAbrir.png")); // NOI18N
        smAbrirArchivo.setText("Cargar Fichero de Recuperación");
        smAbrirArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smAbrirArchivoActionPerformed(evt);
            }
        });
        mArchivo.add(smAbrirArchivo);

        smGuardarArchivo.setIcon(new javax.swing.ImageIcon("/home/fernando/NetBeansProjects/CuentaHogar_6/iconos/iconoGuardar.png")); // NOI18N
        smGuardarArchivo.setText("Crear Fichero de Recuperación");
        smGuardarArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smGuardarArchivoActionPerformed(evt);
            }
        });
        mArchivo.add(smGuardarArchivo);
        mArchivo.add(jSeparator5);

        smG.setIcon(new javax.swing.ImageIcon("/home/fernando/NetBeansProjects/CuentaHogar_6/iconos/iconoGastos.png")); // NOI18N
        smG.setText("Gastos");
        smG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smGActionPerformed(evt);
            }
        });
        mArchivo.add(smG);

        smT.setIcon(new javax.swing.ImageIcon("/home/fernando/NetBeansProjects/CuentaHogar_6/iconos/iconoTraspasos.png")); // NOI18N
        smT.setText("Traspasos");
        smT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smTActionPerformed(evt);
            }
        });
        mArchivo.add(smT);

        smI.setIcon(new javax.swing.ImageIcon("/home/fernando/NetBeansProjects/CuentaHogar_6/iconos/iconoIngresos.png")); // NOI18N
        smI.setText("Ingresos");
        smI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smIActionPerformed(evt);
            }
        });
        mArchivo.add(smI);
        mArchivo.add(jSeparator8);

        smCambiaCta.setIcon(new javax.swing.ImageIcon("/home/fernando/NetBeansProjects/CuentaHogar/iconos/iconoCambioCuenta.gif")); // NOI18N
        smCambiaCta.setText("Cambiar de Cuenta");
        smCambiaCta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smCambiaCtaActionPerformed(evt);
            }
        });
        mArchivo.add(smCambiaCta);
        mArchivo.add(jSeparator7);

        smSalir.setIcon(new javax.swing.ImageIcon("/home/fernando/NetBeansProjects/CuentaHogar_6/iconos/iconoSalir.png")); // NOI18N
        smSalir.setText("Salir");
        smSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smSalirActionPerformed(evt);
            }
        });
        mArchivo.add(smSalir);

        barraMenu.add(mArchivo);

        mEdicion.setText("Edición");
        mEdicion.setEnabled(false);

        smAddCuenta.setIcon(new javax.swing.ImageIcon("/home/fernando/NetBeansProjects/CuentaHogar_6/iconos/iconoCrearCuenta.png")); // NOI18N
        smAddCuenta.setText("Añadir Cuenta...");
        smAddCuenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smAddCuentaActionPerformed(evt);
            }
        });
        mEdicion.add(smAddCuenta);
        mEdicion.add(jSeparator1);

        smAddTG.setIcon(new javax.swing.ImageIcon("/home/fernando/NetBeansProjects/CuentaHogar_6/iconos/iconoCrearGasto.png")); // NOI18N
        smAddTG.setText("Añadir Tipo Gasto...");
        smAddTG.setToolTipText("");
        smAddTG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smAddTGActionPerformed(evt);
            }
        });
        mEdicion.add(smAddTG);

        smAddTI.setIcon(new javax.swing.ImageIcon("/home/fernando/NetBeansProjects/CuentaHogar_6/iconos/iconoCrearIngreso.png")); // NOI18N
        smAddTI.setText("Añadir Tipo Ingreso...");
        smAddTI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smAddTIActionPerformed(evt);
            }
        });
        mEdicion.add(smAddTI);
        mEdicion.add(jSeparator11);

        smFiltro.setIcon(new javax.swing.ImageIcon("/home/fernando/NetBeansProjects/CuentaHogar_6/iconos/iconoBuscar.png")); // NOI18N
        smFiltro.setText("Buscar");
        smFiltro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smFiltroActionPerformed(evt);
            }
        });
        mEdicion.add(smFiltro);

        barraMenu.add(mEdicion);

        mAyuda.setText("Ayuda");
        mAyuda.setEnabled(false);

        subMenuAcercaDe.setIcon(new javax.swing.ImageIcon("/home/fernando/NetBeansProjects/CuentaHogar/iconos/iconoInfo.png")); // NOI18N
        subMenuAcercaDe.setText("Acerca de...");
        subMenuAcercaDe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuAcercaDeActionPerformed(evt);
            }
        });
        mAyuda.add(subMenuAcercaDe);

        subMenuInstrucc.setIcon(new javax.swing.ImageIcon("/home/fernando/NetBeansProjects/CuentaHogar/iconos/iconoInstrucciones.gif")); // NOI18N
        subMenuInstrucc.setText("Instrucciones");
        subMenuInstrucc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuInstruccActionPerformed(evt);
            }
        });
        mAyuda.add(subMenuInstrucc);

        barraMenu.add(mAyuda);

        setJMenuBar(barraMenu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(pGral, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pGral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCtasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCtasActionPerformed
        this.cargarTablaCtas();
    }//GEN-LAST:event_btnCtasActionPerformed

    private void btnSalirVCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirVCActionPerformed
        this.salirVentanaCtas();
    }//GEN-LAST:event_btnSalirVCActionPerformed

    private void btnTipoMovActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTipoMovActionPerformed
        this.mostrarVentanaMov();
    }//GEN-LAST:event_btnTipoMovActionPerformed

    private void btnSalirVMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirVMActionPerformed
        this.salirVentanaTipoMov();
    }//GEN-LAST:event_btnSalirVMActionPerformed

    private void btnAceptarMovActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarMovActionPerformed
        this.crearMov(despCtas, despMov, txtFechaMov, txtImpMov);
    }//GEN-LAST:event_btnAceptarMovActionPerformed

    private void btn2pGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn2pGActionPerformed
        this.gestionarBtns2p(B2PG);
    }//GEN-LAST:event_btn2pGActionPerformed

    private void btn2pIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn2pIActionPerformed
        this.gestionarBtns2p(B2PI);
    }//GEN-LAST:event_btn2pIActionPerformed

    private void btnEliminarTMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarTMActionPerformed
        this.gestionarElimnarFilaTabla();
    }//GEN-LAST:event_btnEliminarTMActionPerformed

    private void subMenuEmEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuEmEliminarActionPerformed
        this.gestionarElimnarFilaTabla();
    }//GEN-LAST:event_subMenuEmEliminarActionPerformed

    private void smAddCuentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smAddCuentaActionPerformed
        this.mostrarVentanaCtas();
    }//GEN-LAST:event_smAddCuentaActionPerformed

    private void smAddTGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smAddTGActionPerformed
        this.gestionarBtns2p(B2PG);
        this.mostrarVentanaMov();
    }//GEN-LAST:event_smAddTGActionPerformed

    private void smAddTIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smAddTIActionPerformed
        this.gestionarBtns2p(B2PI);
        this.mostrarVentanaMov();
    }//GEN-LAST:event_smAddTIActionPerformed

    private void btnEditarTMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarTMActionPerformed
        this.gestionarEditarFilaTabla();
    }//GEN-LAST:event_btnEditarTMActionPerformed

    private void subMenuEmEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuEmEditarActionPerformed
        this.gestionarEditarFilaTabla();
    }//GEN-LAST:event_subMenuEmEditarActionPerformed

    private void btnCancelarVEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarVEActionPerformed
        this.gestionarCancelarEditarFilaTabla();
    }//GEN-LAST:event_btnCancelarVEActionPerformed

    private void btnAceptarVEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarVEActionPerformed
        this.gestionarAceptarEditarFilaTabla();
    }//GEN-LAST:event_btnAceptarVEActionPerformed

    private void tMovMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tMovMousePressed
        this.gestionarBtns2p(B2PTM);
    }//GEN-LAST:event_tMovMousePressed

    private void btn2pTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn2pTActionPerformed
        this.gestionarBtns2p(B2PT);
    }//GEN-LAST:event_btn2pTActionPerformed

    private void smSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smSalirActionPerformed
        this.salirVentanaPrincipal();
    }//GEN-LAST:event_smSalirActionPerformed

    private void smIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smIActionPerformed
        this.gestionarBtns2p(B2PI);
    }//GEN-LAST:event_smIActionPerformed

    private void smGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smGActionPerformed
        this.gestionarBtns2p(B2PG);
    }//GEN-LAST:event_smGActionPerformed

    private void smTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smTActionPerformed
        this.gestionarBtns2p(B2PT);
    }//GEN-LAST:event_smTActionPerformed

    private void btnAceptarTrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarTrasActionPerformed
        this.crearMov(despTrasOrigen, despTrasDest, txtFechaTras, txtImpTras);
    }//GEN-LAST:event_btnAceptarTrasActionPerformed

    private void btn2pFiltroTMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn2pFiltroTMActionPerformed
        this.gestionarBtns2p(B2PFTM);
    }//GEN-LAST:event_btn2pFiltroTMActionPerformed

    private void txtFiltroCol1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFiltroCol1KeyReleased
        this.cargarFiltroTM();
    }//GEN-LAST:event_txtFiltroCol1KeyReleased

    private void txtFiltroCol2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFiltroCol2KeyReleased
        this.cargarFiltroTM();
    }//GEN-LAST:event_txtFiltroCol2KeyReleased

    private void txtFiltroCol3AnyoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFiltroCol3AnyoKeyReleased
        this.cargarFiltroTM();
    }//GEN-LAST:event_txtFiltroCol3AnyoKeyReleased

    private void txtFiltroCol4KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFiltroCol4KeyReleased
        this.cargarFiltroTM();
    }//GEN-LAST:event_txtFiltroCol4KeyReleased

    private void smFiltroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smFiltroActionPerformed
        btn2pFiltroTM.isSelected();
        this.gestionarBtns2p(B2PFTM);
    }//GEN-LAST:event_smFiltroActionPerformed

    private void btnImprimirTMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirTMActionPerformed
        try {
            ed.imprimirTabla(pTM, tMov, "/reportes/Imprimir.jrxml", txtTotal.getText());
        } catch (Exception ex) {
            Logger.getLogger(ElementosGraficos.class.getName()).log(Level.SEVERE, null, ex);
            ed.mostrarError("Fallo al realizar Informe", ex);
        }
    }//GEN-LAST:event_btnImprimirTMActionPerformed

    private void btnGraficaTMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGraficaTMActionPerformed
        try {
            if(this.getTipoMov().equals(sent.getG())){
               ed.imprimirGrafica("/reportes/GraficaG.jrxml"); 
            } else if(this.getTipoMov().equals(sent.getI())){
               ed.imprimirGrafica("/reportes/GraficaI.jrxml"); 
            } else if (this.getTipoMov().equals(sent.getT())) {
               ed.imprimirGrafica("/reportes/GraficaT.jrxml"); 
            }   
        } catch (Exception ex) {
            Logger.getLogger(ElementosGraficos.class.getName()).log(Level.SEVERE, null, ex);
            ed.mostrarError("Fallo al realizar Grafica", ex);
        }
  
    }//GEN-LAST:event_btnGraficaTMActionPerformed

    private void despFiltroCol3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_despFiltroCol3ActionPerformed
        this.cargarFiltroTM();
    }//GEN-LAST:event_despFiltroCol3ActionPerformed

    private void despFiltroCol4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_despFiltroCol4ActionPerformed
        this.cargarFiltroTM();
    }//GEN-LAST:event_despFiltroCol4ActionPerformed

    private void checkNumCuentaVCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkNumCuentaVCActionPerformed
        if (checkNumCuentaVC.isSelected()) {
            this.estadoNumeroCta(true);
        } else if (!checkNumCuentaVC.isSelected()) {
            this.estadoNumeroCta(false);
        }
    }//GEN-LAST:event_checkNumCuentaVCActionPerformed

    private void tablaCuentasVCMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaCuentasVCMousePressed
        this.activarTablaCtas();
    }//GEN-LAST:event_tablaCuentasVCMousePressed

    private void btnNuevaVCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaVCActionPerformed
        this.desactivarTablaCtas();
    }//GEN-LAST:event_btnNuevaVCActionPerformed

    private void tTipoMovVMMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tTipoMovVMMousePressed
        this.activarTablaTipoMov();
    }//GEN-LAST:event_tTipoMovVMMousePressed

    private void btnNuevoVMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoVMActionPerformed
        this.desactivarTablaTipoMov();
    }//GEN-LAST:event_btnNuevoVMActionPerformed

    private void btnCrearVMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearVMActionPerformed
        this.crearTipoMov();
    }//GEN-LAST:event_btnCrearVMActionPerformed

    private void btnCrearVCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearVCActionPerformed
        this.crearCta();
    }//GEN-LAST:event_btnCrearVCActionPerformed

    private void btnEliminarVCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarVCActionPerformed
        this.gestionarElimnarFilaTabla();
    }//GEN-LAST:event_btnEliminarVCActionPerformed

    private void btnEliminarVMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarVMActionPerformed
        this.gestionarElimnarFilaTabla();
    }//GEN-LAST:event_btnEliminarVMActionPerformed

    private void btnEditarVCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarVCActionPerformed
        this.gestionarEditarFilaTabla();
    }//GEN-LAST:event_btnEditarVCActionPerformed

    private void btnEditarVMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarVMActionPerformed
        this.gestionarEditarFilaTabla();
    }//GEN-LAST:event_btnEditarVMActionPerformed

    private void txtImpMovKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtImpMovKeyReleased
        ed.validarImporte(txtImpMov);
    }//GEN-LAST:event_txtImpMovKeyReleased

    private void txtImpTrasKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtImpTrasKeyReleased
        ed.validarImporte(txtImpTras);
    }//GEN-LAST:event_txtImpTrasKeyReleased

    private void txtCapitalInicialVCKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCapitalInicialVCKeyReleased
        ed.validarImporte(txtCapitalInicialVC);
    }//GEN-LAST:event_txtCapitalInicialVCKeyReleased

    private void txtNumCuentaVC1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumCuentaVC1KeyReleased
        ed.validarCta(txtNumCuentaVC1, 4);
    }//GEN-LAST:event_txtNumCuentaVC1KeyReleased

    private void txtNumCuentaVC2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumCuentaVC2KeyReleased
        ed.validarCta(txtNumCuentaVC2, 2);
    }//GEN-LAST:event_txtNumCuentaVC2KeyReleased

    private void txtNumCuentaVC3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumCuentaVC3KeyReleased
        ed.validarCta(txtNumCuentaVC3, 4);
    }//GEN-LAST:event_txtNumCuentaVC3KeyReleased

    private void txtNumCuentaVC4KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumCuentaVC4KeyReleased
        ed.validarCta(txtNumCuentaVC4, 10);
    }//GEN-LAST:event_txtNumCuentaVC4KeyReleased

    private void smGuardarArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smGuardarArchivoActionPerformed
        this.abrirSelectorArchivos("Guardar");
    }//GEN-LAST:event_smGuardarArchivoActionPerformed

    private void smAbrirArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smAbrirArchivoActionPerformed
        this.abrirSelectorArchivos("Abrir");
    }//GEN-LAST:event_smAbrirArchivoActionPerformed

    private void txtFiltroCol3DiaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFiltroCol3DiaKeyReleased
        this.cargarFiltroTM();
    }//GEN-LAST:event_txtFiltroCol3DiaKeyReleased

    private void txtFiltroCol3MesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFiltroCol3MesKeyReleased
        this.cargarFiltroTM();
    }//GEN-LAST:event_txtFiltroCol3MesKeyReleased

    private void subMenuAcercaDeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuAcercaDeActionPerformed
        vAcercaDe.setVisible(true);
    }//GEN-LAST:event_subMenuAcercaDeActionPerformed

    private void btnGitHubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGitHubActionPerformed
        try {
            ed.abrirPaginaWeb(new URL("https://github.com/fernando31/CuentaHogar.git").toURI());
        } catch (Exception ex) {
            Logger.getLogger(ElementosGraficos.class.getName()).log(Level.SEVERE, null, ex);
            ed.mostrarError("Fallo abrir la página web", ex);
        }
    }//GEN-LAST:event_btnGitHubActionPerformed

    private void btnSalirVAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirVAActionPerformed
       this.gestionarSalirVent(vAcercaDe);
    }//GEN-LAST:event_btnSalirVAActionPerformed

    private void btnJavaDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJavaDocActionPerformed
        try {
            ed.abrirFichero("documentacion/javadoc/index.html");
        } catch (Exception ex) {
            ed.mostrarError("Fallo abrir el JavaDoc", ex);
        }
    }//GEN-LAST:event_btnJavaDocActionPerformed

    private void smCambiaCtaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smCambiaCtaActionPerformed
        VentanaInicial vIni = new VentanaInicial();
        vIni.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_smCambiaCtaActionPerformed

    private void subMenuInstruccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuInstruccActionPerformed
        try {
            ed.abrirFichero("documentacion/Instrucciones_CuentaHogar.pdf");
        } catch (Exception ex) {
            ed.mostrarError("Fallo al abrir la Instrucciones", ex);
        }        
    }//GEN-LAST:event_subMenuInstruccActionPerformed

    private void btnDocumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDocumActionPerformed
        try {
            ed.abrirFichero("documentacion/documentacion_CuentaHogar.pdf");
        } catch (Exception ex) {
            ed.mostrarError("Fallo al abrir la Documentación", ex);
        }  
    }//GEN-LAST:event_btnDocumActionPerformed

    /**
     * @param args the command line arguments
     */
 //   public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
   /*     try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ElementosGraficos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ElementosGraficos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ElementosGraficos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ElementosGraficos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }*/
        //</editor-fold>

        /* Create and display the form */
     /*   java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new ElementosGraficos().setVisible(true);
                } catch (Exception ex) {
                    Logger.getLogger(ElementosGraficos.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, "FALLO GRAVE!!\n Imposible arrancar ElementosGraficos", "ERROR", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
            }
        });*/
 //   }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar barraHerramientas;
    private javax.swing.JMenuBar barraMenu;
    private javax.swing.JToggleButton btn2pFiltroTM;
    private javax.swing.JToggleButton btn2pG;
    private javax.swing.JToggleButton btn2pI;
    private javax.swing.JToggleButton btn2pT;
    private javax.swing.JToggleButton btn2pTM;
    private javax.swing.JButton btnAceptarMov;
    private javax.swing.JButton btnAceptarTras;
    private javax.swing.JButton btnAceptarVE;
    private javax.swing.JButton btnCancelarVE;
    private javax.swing.JButton btnCrearVC;
    private javax.swing.JButton btnCrearVM;
    private javax.swing.JButton btnCtas;
    private javax.swing.JButton btnDocum;
    private javax.swing.JButton btnEditarTM;
    private javax.swing.JButton btnEditarVC;
    private javax.swing.JButton btnEditarVM;
    private javax.swing.JButton btnEliminarTM;
    private javax.swing.JButton btnEliminarVC;
    private javax.swing.JButton btnEliminarVM;
    private javax.swing.JButton btnGitHub;
    private javax.swing.JButton btnGraficaTM;
    private javax.swing.JButton btnImprimirTM;
    private javax.swing.JButton btnJavaDoc;
    private javax.swing.JButton btnNuevaVC;
    private javax.swing.JButton btnNuevoVM;
    private javax.swing.JButton btnSalirVA;
    private javax.swing.JButton btnSalirVC;
    private javax.swing.JButton btnSalirVM;
    private javax.swing.JButton btnTipoMov;
    private javax.swing.JCheckBox checkNumCuentaVC;
    private javax.swing.JComboBox despCtas;
    private javax.swing.JComboBox despFiltroCol3;
    private javax.swing.JComboBox despFiltroCol4;
    private javax.swing.JComboBox despMov;
    private javax.swing.JComboBox despTrasDest;
    private javax.swing.JComboBox despTrasOrigen;
    private javax.swing.JLabel etCtas;
    private javax.swing.JLabel etEur;
    private javax.swing.JLabel etEur3;
    private javax.swing.JLabel etFechaMov;
    private javax.swing.JLabel etFechaTras;
    private javax.swing.JLabel etFiltroCol1;
    private javax.swing.JLabel etFiltroCol2;
    private javax.swing.JLabel etFiltroCol3;
    private javax.swing.JLabel etFiltroCol4;
    private javax.swing.JLabel etImpMov;
    private javax.swing.JLabel etImpTras;
    private javax.swing.JLabel etTMovVM;
    private javax.swing.JLabel etTipoMov;
    private javax.swing.JLabel etTotal;
    private javax.swing.JLabel etTrasDest;
    private javax.swing.JLabel etTrasOrigen;
    private javax.swing.JLabel etiqCapitalInicialVC;
    private javax.swing.JLabel etiqCuentaVC;
    private javax.swing.JLabel etiqEurVC;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPpMenuTablas;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JToolBar.Separator jSeparator13;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JMenu mArchivo;
    private javax.swing.JMenu mAyuda;
    private javax.swing.JMenu mEdicion;
    private javax.swing.JPanel pBajoMov;
    private javax.swing.JPanel pBajoTM;
    private javax.swing.JPanel pBajoTMovVM;
    private javax.swing.JPanel pBajoTTMovVM;
    private javax.swing.JPanel pBajoTras;
    private javax.swing.JPanel pFiltro;
    private javax.swing.JPanel pGral;
    private javax.swing.JPanel pGralVM;
    private javax.swing.JPanel pMov;
    private javax.swing.JPanel pTM;
    private javax.swing.JPanel pTMovVM;
    private javax.swing.JPanel pTras;
    private javax.swing.JPanel panelBajoCuentasVC;
    private javax.swing.JPanel panelBajoEditarVE;
    private javax.swing.JPanel panelCuentasVC;
    private javax.swing.JPanel panelCuentasVCNumCta;
    private javax.swing.JPanel panelEditarVE;
    private javax.swing.JPanel panelGeneralVC;
    private javax.swing.JPanel panleBajoTablaCuentasVC;
    private javax.swing.JMenuItem smAbrirArchivo;
    private javax.swing.JMenuItem smAddCuenta;
    private javax.swing.JMenuItem smAddTG;
    private javax.swing.JMenuItem smAddTI;
    private javax.swing.JMenuItem smCambiaCta;
    private javax.swing.JMenuItem smFiltro;
    private javax.swing.JMenuItem smG;
    private javax.swing.JMenuItem smGuardarArchivo;
    private javax.swing.JMenuItem smI;
    private javax.swing.JMenuItem smSalir;
    private javax.swing.JMenuItem smT;
    private javax.swing.JMenuItem subMenuAcercaDe;
    private javax.swing.JMenuItem subMenuEmEditar;
    private javax.swing.JMenuItem subMenuEmEliminar;
    private javax.swing.JMenuItem subMenuInstrucc;
    private javax.swing.JTable tMov;
    private javax.swing.JTable tTipoMovVM;
    private javax.swing.JTable tablaCuentasVC;
    private javax.swing.JTable tablaEditar;
    private javax.swing.JTextField txtCapitalInicialVC;
    private javax.swing.JTextField txtCuentaVC;
    private com.toedter.calendar.JDateChooser txtFechaMov;
    private com.toedter.calendar.JDateChooser txtFechaTras;
    private javax.swing.JTextField txtFiltroCol1;
    private javax.swing.JTextField txtFiltroCol2;
    private javax.swing.JTextField txtFiltroCol3Anyo;
    private javax.swing.JTextField txtFiltroCol3Dia;
    private javax.swing.JTextField txtFiltroCol3Mes;
    private javax.swing.JTextField txtFiltroCol4;
    private javax.swing.JTextField txtImpMov;
    private javax.swing.JTextField txtImpTras;
    private javax.swing.JTextField txtNumCuentaVC1;
    private javax.swing.JTextField txtNumCuentaVC2;
    private javax.swing.JTextField txtNumCuentaVC3;
    private javax.swing.JTextField txtNumCuentaVC4;
    private javax.swing.JTextField txtTMovVM;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JFrame vAcercaDe;
    private javax.swing.JFrame vCtas;
    private javax.swing.JFrame vEditar;
    private javax.swing.JFrame vTMov;
    // End of variables declaration//GEN-END:variables
}
