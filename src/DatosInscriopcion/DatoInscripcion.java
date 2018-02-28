/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DatosInscriopcion;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.border.Border;

/**
 *
 * @author OscarFernandezRodriguez
 */
public class DatoInscripcion extends javax.swing.JFrame {

    /*Lo usare para calcular y validar la fecha, simplemente son dos arrays con la misma dimension,
    cuando uno este en una posicion concreta, con el otro sabremos si el numero de mes es correcto*/
    static int[] diasMes = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    static int[] numeroMes = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    Font negrita = new Font("Arial", Font.BOLD, 14);    //Creo una fuente de negrita
    //Lo usare para recuperar el estado original cada vez que das a aceptar si se cumplen los requisitos del campo
    Border volverBordeOriginal = BorderFactory.createLineBorder(new Color(0, 0, 51));
    //Lo usare para cambiar el borde de los jformatted que sean erroneos sus datos
    Border bordeRojoError = BorderFactory.createLineBorder(Color.red);
    private Object bean;

    /**
     * Creates new customizer CompDNI
     */
    public DatoInscripcion() {
        initComponents();
        this.setLocationRelativeTo(null);
        Edad.setVisible(false);                     //Este es el lugar donde se mostrara la edad si es correcta
        textoMensajeERROR.setVisible(false);        //Esto es un jlabel oculto, aqui aparecen los mensajes de error
        TipoVia.setFont(negrita);                    //Pongo a negrita el JComboBox
        Edad.setText("");
        textoMensajeERROR.setText("<html><body>");  //Empieza el html para poder hacer los saltos de linea en el Jlabel oculto donde saldran los mensajes de error
        Hombre.setActionCommand("Hombre");          //Seteo los dos JbuttonRadio
        Mujer.setActionCommand("Mujer");
        NoSabe.setActionCommand("No contesta");
    }

    public void setObject(Object bean) {
        this.bean = bean;
    }

    static String ponCerosIzquierda(String str, int longitud) {
        String ceros = "";
        if (str.length() < longitud) {
            for (int i = 0; i < (longitud - str.length()); i++) {
                ceros = ceros + '0';
            }
            str = ceros + str;
        }
        return str;
    }

    /*Compruebo la Cuenta Corriente y calculo el IBAN si esta es correcta*/
    public void comprobarCuentaCorriente() {
        /*PARA CALCULAR EL PRIMER DÍGITO DE CONTROL
        Una cuenta se compone:
        EEEE OOOO DD NNNNNNNNNN
        E- entidad
        o- oficina
        d- dígitos de control
        n - número de la cuenta
        tienes que añadir dos ceros al principio y coger los número de entidad y oficina, de tal
        modo que te quede un número de 10 dígitos.
        A continuación tienes que multiplicar cada dígito que te dio anteriormente 
        ( es decir, 00 eeee oooo) por los siguientes números según el orden: 1, 2, 4, 8, 5, 10, 
        9, 7, 3, 6 
        El resultado de cada multiplicación tiene que sumarlo (es decir hacer un sumatorio)
        Ese resultado debes dividirlo entre 11
        El resto de esta división es el dígito de control // excepto en los siguientes casos:
        A 11 le quitamos el resto anterior, y ese el el primer dígito de control, con la salvedad de que si nos da 10, el dígito es 1

        Para obtener el segundo dígito de control:
        multiplico el resultado de los 10 ultimos digitos de la cuenta por la siguiente serie 1,2,4,8,5,10,9,7,3,6
        y actuo igual que con el digito 1
        Se suman todos los resultados obtenidos.
        Se divide entre 11 y nos quedamos con el resto de la división.
        A 11 le quitamos el resto anterior, y ese el el segundo dígito de control, con la salvedad de que si nos da 10, el dígito es 1
         */
        String IBANUsuario = NumeroCuentaCorriente.getText();
        if (!IBANUsuario.equals("                       ")) {
            String letraE = "14";   //para españa que es la E
            String LetraS = "28";   //para españa le sigue la S

            //Corto la cadena con split
            String[] partesIban = IBANUsuario.split(" ");  //Rompo la cadena
            String IbanEntidad = partesIban[0];
            String IbanOficina = partesIban[1];
            String IbanDC = partesIban[2];
            String IbanNumeroCuenta = partesIban[3];

            //Compruebo que la cuenta corriente pasada sea verdadera, obtengo el primer digito de control
            String entidadYOfina = "00" + IbanEntidad + IbanOficina;
            int guardoValoresParaSumarlo = 0;
            String[] splitEntidadYOficina = entidadYOfina.split("");
            int[] numeroMultiplicar = {1, 2, 4, 8, 5, 10, 9, 7, 3, 6};
            int sumatario10PrimerosDigitos = 0;  // Si esestos digitos dan 0 y el otro sumatario tambien,
            //la cuenta es falsa
            for (int i = 0; splitEntidadYOficina.length > i; i++) {
                sumatario10PrimerosDigitos += Integer.parseInt(splitEntidadYOficina[i]);
                guardoValoresParaSumarlo += Integer.parseInt(splitEntidadYOficina[i]) * numeroMultiplicar[i];
            }
            int primerDigitoControl = 11 - guardoValoresParaSumarlo % 11;

            /*COMPRUEBO QUE EL DIGITO DE CONTROL 1 NO SEA 10 o 11*/
            if (primerDigitoControl == 10) {
                primerDigitoControl = 1;
            } else if (primerDigitoControl == 11) {
                primerDigitoControl = 0;
            }

            //Compruebo el segundo digito de control
            String[] splitUltimos10Digitos = IbanNumeroCuenta.split("");
            guardoValoresParaSumarlo = 0;
            int sumatario10UltimosDigitos = 0;  // Si esestos digitos dan 0 y el otro sumatario tambien,
            //la cuenta es falsa
            for (int i = 0; splitUltimos10Digitos.length > i; i++) {
                sumatario10UltimosDigitos += Integer.parseInt(splitUltimos10Digitos[i]);
                guardoValoresParaSumarlo += Integer.parseInt(splitUltimos10Digitos[i]) * numeroMultiplicar[i];
            }
            int segundoDigitoControl = 11 - guardoValoresParaSumarlo % 11;

            /*COMPRUEBO QUE EL DIGITO DE CONTROL 1 NO SEA 10 o 11*/
            if (segundoDigitoControl == 10) {
                segundoDigitoControl = 1;
            } else if (segundoDigitoControl == 11) {
                segundoDigitoControl = 0;
            }
            //Paso a string para juntar el digito 1 y el 2 calculado, en el siguiente if compruebo que el digito de control pasado
            //sea igual al digito de control calculado (el que tiene que ser) sino lo es, arroja un error ya que los datos no son correctos
            String digitoControlCalculado = String.valueOf(primerDigitoControl) + String.valueOf(segundoDigitoControl);

            if (Integer.parseInt(IbanDC) == Integer.parseInt(digitoControlCalculado)
                    && sumatario10PrimerosDigitos != 0 && segundoDigitoControl != 0) {
                String numeroIBANReal = IbanEntidad + IbanOficina + IbanDC + IbanNumeroCuenta + letraE + LetraS + "00";
                /*Calculo el modulo de 97 y al resultado le resto 98*/
                BigInteger numeroCuenta = new BigInteger(numeroIBANReal);
                BigInteger noventaysiete = new BigInteger("97");
                numeroCuenta = numeroCuenta.mod(noventaysiete);
                int digitoControlTeorico = numeroCuenta.intValue();
                digitoControlTeorico = 98 - digitoControlTeorico;
                String digitoControlString = ponCerosIzquierda(Integer.toString(digitoControlTeorico), 2);
                Iban.setFont(negrita);
                Iban.setForeground(Color.white);    //Esto igual esta prohibido
                Iban.setText("ES" + digitoControlString);
                Iban.setBorder(volverBordeOriginal);
                NumeroCuentaCorriente.setBorder(volverBordeOriginal);

            } else {
                textoMensajeERROR.setText(textoMensajeERROR.getText() + "-Error, <font color=\"red\">Cuenta Corriente</font> INCORRECTA, no existe <br> ");
                textoMensajeERROR.setForeground(Color.white);    //Esto igual esta prohibido
                textoMensajeERROR.setFont(negrita);
                textoMensajeERROR.setVisible(true);
                NumeroCuentaCorriente.setBorder(bordeRojoError);
            }
        } else {
            textoMensajeERROR.setText(textoMensajeERROR.getText() + "-No puedes dejar la <font color=\"red\">Cuenta Corriente</font> sin rellenar<br>");
            textoMensajeERROR.setForeground(Color.white);    //Esto igual esta prohibido
            textoMensajeERROR.setFont(negrita);
            textoMensajeERROR.setVisible(true);
            NumeroCuentaCorriente.setBorder(bordeRojoError);
        }
    }

    public void comprobarDNI() {
        /*"La letra del DNI se obtiene dividiendo el número completo de nuestro DNI entre 23
           y al resto de dicha división que deberá estar comprendido entre 0 y 22 se le asigna la
           letra según la equivalencia de la siguiente tabla:
        RESTO	0	1	2	3	4	5	6	7	8	9	10	11
        LETRA	T	R	W	A	G	M	Y	F	P	D	X	B
 

        RESTO	12	13	14	15	16	17	18	19	20	21	22
        LETRA	N	J	Z	S	Q	V	H	L	C	K	E
         */

        String letrasSegunResto = "TRWAGMYFPDXBNJZSQVHLCKE";
        String dniUsuarioNumeros = Dni.getText();
        String[] partesDni = dniUsuarioNumeros.split("-");  //Rompo la cadena
        String dniNumero = partesDni[0];
        String dniLetra = partesDni[1];
        if (!dniUsuarioNumeros.equals("        - ")) {

            int modulo = Integer.parseInt(dniNumero) % 23;   //Consigo el numero y hago el modulo de 23
            char letra = letrasSegunResto.charAt(modulo);

            if (dniLetra.equals(Character.toString(letra))) {
                Dni.setBorder(volverBordeOriginal);
            } else {

                textoMensajeERROR.setText(textoMensajeERROR.getText() + "-Error en <font color=\"red\"> DNI</font>, no existe <br> ");
                textoMensajeERROR.setFont(negrita);
                textoMensajeERROR.setForeground(Color.white);    //Esto igual esta prohibido
                textoMensajeERROR.setVisible(true);
                Dni.setBorder(bordeRojoError);
            }
        } else {
            textoMensajeERROR.setText(textoMensajeERROR.getText() + "-Error en <font color=\"red\"> DNI </font>, faltan caracteres <br> ");
            textoMensajeERROR.setFont(negrita);
            textoMensajeERROR.setForeground(Color.white);    //Esto igual esta prohibido
            textoMensajeERROR.setVisible(true);
            Dni.setBorder(bordeRojoError);
        }
    }

    public void comprobarCodigoPostal() {
        String[] nombreProvincia = {"Alava", "Albacete", "Alicante", "Almería", "Ávila", "Badajoz", "Baleares", "Barcelona", "Burgos", "Cáceres",
            "Cádiz", "Castellón", "Ciudad Real", "Córdoba", "La Coruña", "Cuenca", "Gerona", "Granada", "Guadalajara",
            "Guipúzcoa", "Huelva", "Huesca", "Jaén", "León", "Lérida", "Logroño", "Lugo", "Madrid", "Málaga", "Murcia", "Navarra",
            "Orense", "Astturias(Oviedo)", "Palencia", "Las Palmas", "Pontevedra", "Salamanca", "S.C Tenerife", "Cantabria(Santander)", "Segovia", "Sevilla", "Soria", "Tarragona",
            "Teruel", "Toledo", "Valencia", "Valladolid", "Vizcaya", "Zamora", "Zaragoza", "Ceuta", "Melilla"};

        String[] numeroProvincias = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "12", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34",
            "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52"};

        String[] minimoCodigo = {"01000", "02000", "03000", "04000", "05000", "06000", "07000", "08000", "09000", "10000", "11000", "12000", "13000", "14000", "15000", "16000", "17000", "18000", "19000", "20000", "21000", "22000", "23000", "24000", "25000", "26000", "27000", "28000", "29000", "30000", "31000", "32000", "33000", "34000", "35000", "36000", "37000", "38000", "39000", "40000", "41000", "42000", "43000", "44000", "45000", "46000", "47000", "48000", "49000", "50000", "51000", "52000"};
        String[] maximoCodigo = {"01520", "02696", "03860", "04897", "05697", "06980", "07860", "08980", "09693", "10991", "11693", "12609", "13779", "14970", "15981", "16891", "17869", "18890", "19495", "20870", "21891", "22880", "23790", "24996", "25796", "26589", "27891", "28991", "29792", "30892", "31890", "32930", "33993", "34889", "35640", "36980", "37900", "38911", "39880", "40593", "41980", "42368", "43896", "44793", "45960", "46980", "47883", "48992", "49882", "50840", "51001", "52001"};
        boolean provinciaEncontrada = false;
        /*Obtengo los dos primeros números de la cadena para poder trabajar*/
        String codigoPostalUsuario = CodigoPostal.getText();
        /*Compruebo que el contenido introducido no este vacio*/
        if (!codigoPostalUsuario.equals("     ")) {
            String codigoPostalUsuarioProvincia = codigoPostalUsuario.substring(0, 2);

            /*Convierto los datos a enteros para trabajar con ellos en el 2º if*/
            int enteroCodigoPostalUsuario = Integer.parseInt(codigoPostalUsuario);

            /*Recorro el array numeroProvincia para comprobar si existe ese numero y cual es*/
            for (int i = 0; i < numeroProvincias.length; i++) {
                if (codigoPostalUsuarioProvincia.equals(numeroProvincias[i])) {
                    provinciaEncontrada = true;
                    /*Compruebo que el numero pasado esta entre el minimo y el máximo*/
                    int enteroMinimoCodigoTemporal = Integer.parseInt(minimoCodigo[i]);
                    int enteroMaximoCodigoTemporal = Integer.parseInt(maximoCodigo[i]);

                    if (enteroMinimoCodigoTemporal <= enteroCodigoPostalUsuario && enteroMaximoCodigoTemporal >= enteroCodigoPostalUsuario) {
                        mostrarProvincia.setText("Provincia: " + nombreProvincia[i]);
                        mostrarProvincia.setFont(negrita);
                        mostrarProvincia.setForeground(Color.white);    //Esto igual esta prohibido
                        CodigoPostal.setBorder(volverBordeOriginal);

                    } else {
                        textoMensajeERROR.setText(textoMensajeERROR.getText() + "-Error en <font color=\"red\"> C.P</font>, revisa tu C.P <br>");
                        textoMensajeERROR.setForeground(Color.white);    //Esto igual esta prohibido
                        textoMensajeERROR.setFont(negrita);
                        textoMensajeERROR.setEnabled(true);
                        CodigoPostal.setBorder(bordeRojoError);
                    }
                }
            }
        }
        /*Sino encuentra provincia en el for, es que no existe el indice de provincia*/
        if (provinciaEncontrada==false) {
            textoMensajeERROR.setText(textoMensajeERROR.getText() + "-Error,<font color=\"red\"> Provincia, </font> no encontrada, revisa tu C.P <br>");
            textoMensajeERROR.setForeground(Color.white);    //Esto igual esta prohibido
            textoMensajeERROR.setFont(negrita);
            textoMensajeERROR.setEnabled(true);
            CodigoPostal.setBorder(bordeRojoError);
        }

    }

    /*Calculo la fecha, el ejercicio manda calcular la fecha para mayores de edad y menores de 70 años*/
    public void fechaIntroducida() {
        //LA FECHA VA DESDE 18 AÑOS A 70 AÑOS, SI SE QUIERE OTRA EDAD, SOLO HAY QUE CAMBIAR ESTOS LITERALES
        //NO USO CONSTANTES PORQUE SOLO LO UTILIZO EN ESTE MÉTODO Y EL CÓDIGO ES MAS LEGIBLE
        int annoMenor = 18;
        int annoMaximo = 70;
        int edadCalculada = 0;

        String textoMostrar = "Formato no válido " + FechaNacimiento.getText();
        boolean fechaNacimientoCorrecta = false;

        /*Recogo los datos que ha pasado el usuario*/
        String fechaPasada = FechaNacimiento.getText();
        String[] partesFecha = fechaPasada.split("/");  //Rompo la cadena
        String diaString = partesFecha[0];
        String mesString = partesFecha[1];
        String annoString = partesFecha[2];

        /*Compruebo que los datos no esten vacios*/
        if (!diaString.equals("  ") || !mesString.equals("  ") || !annoString.equals("    ")) {

            /*Los paso a enteros*/
            int dia = Integer.parseInt(diaString);
            int mes = Integer.parseInt(mesString);
            int anno = Integer.parseInt(annoString);

            /*Descubrimos que dia es hoy*/
            Date fecha = new Date();
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            String hoy = formato.format(fecha);

            /*Compruebo la fecha de hoy*/
            String[] fechaHoy = hoy.split("/");
            String diaHoyString = fechaHoy[0];
            String mesHoyString = fechaHoy[1];
            String annoHoyString = fechaHoy[2];

            /*Los paso a int*/
            int diaHoy = Integer.parseInt(diaHoyString);
            int mesHoy = Integer.parseInt(mesHoyString);
            int annoHoy = Integer.parseInt(annoHoyString);

            int diferenciaAyo = annoHoy - anno;
            int diferenciaMes = mesHoy - mes;
            int diferenciaDia = diaHoy - dia;
            //Si está en ese año pero todavía no los ha cumplido
            if (diferenciaMes < 0 || (diferenciaMes == 0 && diferenciaDia < 0)) {
                diferenciaAyo = diferenciaAyo - 1; //no aparecían los dos guiones del postincremento :|
            }
            edadCalculada = diferenciaAyo;
            //Compruebo si el dia es correcto
            for (int i = 0; i < numeroMes.length; i++) {    //Recorro como máximo 12 veces
                /*Compruebo que el año introducido este en los valores correctos*/
                if (edadCalculada >= annoMenor && edadCalculada <= annoMaximo) {
                    /*no entramos hasta que el mes introducido este entre 01 al 12, sino sale*/
                    if (mes == numeroMes[i]) {
                        /*Compruebo que el dia que han pasado este entre el dia máximo que le pertenece de ese mes y como minimo mayor a 01*/
                        if (diasMes[i] >= dia && dia > 0) {
                            /*Si el año y el mes es el mismo que la fecha actual y el dia pasado mayor que la fecha actual, intentan meter una fecha futura, algo que no puede ser*/
                            fechaNacimientoCorrecta = true; //Si ha llegado aquí la fecha es correcta!                 
                            Edad.setFont(negrita);
                            Edad.setForeground(Color.WHITE);    //Esto igual esta prohibido
                            Edad.setText(String.valueOf(edadCalculada) + " años");
                            Edad.setVisible(true);
                            FechaNacimiento.setBorder(volverBordeOriginal);
                        } else {
                            //Antes de decir que la fecha no es correcta, hay que comprobar bisiestos
                            if (numeroMes[1] == mes) {
                                //SI ((año divisible por 4) Y ((año no divisible por 100) O (año divisible por 400))) ENTONC
                                if ((anno % 4 == 0) && (anno % 100 != 0) && (dia == 29)) {
                                    fechaNacimientoCorrecta = true; //Si ha llegado aquí la fecha es correcta   
                                    Edad.setFont(negrita);
                                    Edad.setForeground(Color.WHITE);    //Esto igual esta prohibido
                                    Edad.setText(String.valueOf(edadCalculada) + " años");
                                    Edad.setVisible(true);
                                    FechaNacimiento.setBorder(volverBordeOriginal);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!fechaNacimientoCorrecta) {
            if (diaString.equals("  ") || mesString.equals("  ") || annoString.equals("    ")) {
                textoMensajeERROR.setFont(negrita);
                textoMensajeERROR.setForeground(Color.white);    //Esto igual esta prohibido
                textoMensajeERROR.setText(textoMensajeERROR.getText() + "-Faltan datos en la <font color=\"red\">fecha</font>");
                textoMensajeERROR.setEnabled(true);
                Edad.setVisible(false);
                FechaNacimiento.setBorder(bordeRojoError);
            } else {
                if (edadCalculada < 0) {
                    textoMensajeERROR.setFont(negrita);
                    textoMensajeERROR.setForeground(Color.white);    //Esto igual esta prohibido
                    textoMensajeERROR.setText(textoMensajeERROR.getText() + "-Error en <font color=\"red\">Fecha, </font>no se puede registrar a no natos<br>");
                    textoMensajeERROR.setEnabled(true);
                    Edad.setVisible(false);
                    FechaNacimiento.setBorder(bordeRojoError);
                } else {
                    if (edadCalculada < annoMenor) {
                        textoMensajeERROR.setFont(negrita);
                        textoMensajeERROR.setForeground(Color.white);    //Esto igual esta prohibido
                        textoMensajeERROR.setText(textoMensajeERROR.getText() + "-Error en <font color=\"red\">Fecha, </font>eres menor de 18 años, tienes " + edadCalculada + " años<br>");
                        textoMensajeERROR.setEnabled(true);
                        Edad.setVisible(false);
                        FechaNacimiento.setBorder(bordeRojoError);
                    }
                }
                if (edadCalculada > annoMaximo) {
                    textoMensajeERROR.setFont(negrita);
                    textoMensajeERROR.setForeground(Color.white);    //Esto igual esta prohibido
                    textoMensajeERROR.setText(textoMensajeERROR.getText() + "-Error en <font color=\"red\">Fecha, </font>eres mayor de 70 años, tienes " + edadCalculada + " años<br>");
                    textoMensajeERROR.setEnabled(true);
                    Edad.setVisible(false);
                    FechaNacimiento.setBorder(bordeRojoError);
                }
            }
        }
    }

    public boolean validarEmail() {
        String email = NombreEmail.getText();
        String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        NombreEmail.setBorder(volverBordeOriginal);
        // Compiles the given regular expression into a pattern.
        Pattern pattern = Pattern.compile(PATTERN_EMAIL);

        // Match the given input against this pattern
        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            textoMensajeERROR.setFont(negrita);
            textoMensajeERROR.setForeground(Color.white);    //Esto igual esta prohibido
            textoMensajeERROR.setText(textoMensajeERROR.getText() + "-El<font color=\"red\"> e-mail</font> no es correcto <br>");
            textoMensajeERROR.setEnabled(true);
            NombreEmail.setBorder(bordeRojoError);
        }
        return matcher.matches();
    }

    public void imrpimirTxt() throws IOException {
        FileWriter fichero = null;
        PrintWriter pw = null;
        String rutaTxt = "datosFormulario.txt";

        //Consigo la primera letra del nombre y la paso a mayusculas
        char primeraLetraNombre = Character.toUpperCase(Nombre.getText().toCharArray()[0]);
        //Creo un objeto String y le meto la letra del nombre en mayusculas, lo siguiente es 
        //un bucle que vaya concatenando cada letra con la anterior
        String nombre = Character.toString(primeraLetraNombre);
        for (int i = 1; i < Nombre.getText().toCharArray().length; i++) {
            nombre += Nombre.getText().toCharArray()[i];
        }

        //Consigo la primera letra del primer Apellido y lo paso a mayusculas
        char primeraLetraApellido1 = Character.toUpperCase(PrimerApellido.getText().toCharArray()[0]);
        //Creo un objeto String y le meto la letra del primer apellido en mayusculas, 
        //lo siguiente es un bucle que vaya concatenando cada letra con la anterior
        String primerApellido = Character.toString(primeraLetraApellido1);
        for (int i = 1; i < PrimerApellido.getText().toCharArray().length; i++) {
            primerApellido += PrimerApellido.getText().toCharArray()[i];
        }

        //Consigo la primera letra del segundo apellido y lo paso a mayusculas
        char primeraLetraApellido2 = Character.toUpperCase(SegundoApellido.getText().toCharArray()[0]);
        //Creo un objeto String y le meto la letra del segundo apellido en mayusculas, 
        //lo siguiente es un bucle que vaya concatenando cada letra con la anterior
        String segundoApellido = Character.toString(primeraLetraApellido2);
        for (int i = 1; i < SegundoApellido.getText().toCharArray().length; i++) {
            segundoApellido += SegundoApellido.getText().toCharArray()[i];
        }

        /*Guardo en el array los datos del formulario*/
        String[] datosFormulario = {nombre, primerApellido, segundoApellido,
            Dni.getText(), NombreEmail.getText(), grupoSexo.getSelection().getActionCommand(), TipoVia.getSelectedItem().toString() + " "
            + DireccionVivienda.getText() + " nº" + numeroDireccion.getText() + " " + numeroPiso.getText() + "º" + letraPuerta.getText().toUpperCase() + " C.P "
            + CodigoPostal.getText() + " " + mostrarProvincia.getText(), Iban.getText() + " " + NumeroCuentaCorriente.getText(), FechaNacimiento.getText(), Edad.getText()};

        String[] mensaje = {"Nombre: ", "Primer Apellido: ", "Segundo Apellido: ",
            "D.N.I: ", "E-mail: ", "Sexo: ",
            "Dirección: ", "IBAN: ", "Fecha Nacimiento: ", "Edad: "};
        try {
            fichero = new FileWriter(rutaTxt, true);
            pw = new PrintWriter(fichero);

            for (int i = 0; i < datosFormulario.length; i++) {
                pw.println(mensaje[i] + datosFormulario[i]);
            }
            pw.println();
            pw.println("#### SIGUIENTE CLIENTE ####");
            pw.println();

            // Icon m = new ImageIcon(getClass().getResource("Icons/todoOK.png"));
            JOptionPane.showMessageDialog(null, "Datos guardados correctamente en  " + rutaTxt, "Datos registrados", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // asegurarnos que se cierra el fichero.
                if (null != fichero) {
                    fichero.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grupoSexo = new javax.swing.ButtonGroup();
        jLabelTitulo = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        Cancelar = new javax.swing.JButton();
        Aceptar = new javax.swing.JButton();
        PanelDatosPersonales1 = new javax.swing.JPanel();
        JlabelEmail8 = new javax.swing.JLabel();
        Dni = new javax.swing.JFormattedTextField();
        JlabelEmail9 = new javax.swing.JLabel();
        JlabelEmail10 = new javax.swing.JLabel();
        NombreEmail = new javax.swing.JTextField();
        JlabelEmail11 = new javax.swing.JLabel();
        JlabelSexo1 = new javax.swing.JLabel();
        Hombre = new javax.swing.JRadioButton();
        Mujer = new javax.swing.JRadioButton();
        JlabelDni2 = new javax.swing.JLabel();
        NoSabe = new javax.swing.JRadioButton();
        PanelBanco1 = new javax.swing.JPanel();
        NumeroCuentaCorriente = new javax.swing.JFormattedTextField();
        jLMostrarIBAN1 = new javax.swing.JLabel();
        JlabelEmail14 = new javax.swing.JLabel();
        JlabelDni3 = new javax.swing.JLabel();
        Iban = new javax.swing.JTextField();
        ImagenNombre1 = new javax.swing.JLabel();
        ImagenSeguridad1 = new javax.swing.JLabel();
        ImagenDomicilio1 = new javax.swing.JLabel();
        ImagenDinero1 = new javax.swing.JLabel();
        PanelDomicilio2 = new javax.swing.JPanel();
        TipoVia = new javax.swing.JComboBox<>();
        DireccionVivienda = new javax.swing.JTextField();
        JlabelAnnos11 = new javax.swing.JLabel();
        numeroDireccion = new javax.swing.JFormattedTextField();
        JlabelAnnos12 = new javax.swing.JLabel();
        numeroPiso = new javax.swing.JFormattedTextField();
        JlabelAnnos13 = new javax.swing.JLabel();
        letraPuerta = new javax.swing.JFormattedTextField();
        JlabelAnnos14 = new javax.swing.JLabel();
        CodigoPostal = new javax.swing.JFormattedTextField();
        mostrarProvincia = new javax.swing.JLabel();
        textoMensajeERROR = new javax.swing.JLabel();
        DatosNombre = new javax.swing.JPanel();
        jLabelNombre3 = new javax.swing.JLabel();
        JlabelPrimerApellido3 = new javax.swing.JLabel();
        jLabelpSegundoApellido2 = new javax.swing.JLabel();
        años2 = new javax.swing.JLabel();
        Nombre = new javax.swing.JTextField();
        PrimerApellido = new javax.swing.JTextField();
        SegundoApellido = new javax.swing.JTextField();
        jLabelNombre4 = new javax.swing.JLabel();
        FechaNacimiento = new javax.swing.JFormattedTextField();
        JlabelFecha2 = new javax.swing.JLabel();
        Edad = new javax.swing.JTextField();
        jCalendar1 = new com.toedter.calendar.JCalendar();
        Fondo = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(742, 700));
        setMinimumSize(new java.awt.Dimension(742, 700));
        setPreferredSize(new java.awt.Dimension(742, 700));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelTitulo.setFont(new java.awt.Font("Consolas", 1, 40)); // NOI18N
        jLabelTitulo.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTitulo.setText("DATOS INSCRIPCIÓN");
        getContentPane().add(jLabelTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 10, -1, -1));
        getContentPane().add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 400, 710, 10));

        Cancelar.setBackground(new java.awt.Color(0, 0, 51));
        Cancelar.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        Cancelar.setForeground(new java.awt.Color(255, 255, 255));
        Cancelar.setText("Cancelar");
        Cancelar.setActionCommand("Aceptar");
        Cancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelarActionPerformed(evt);
            }
        });
        getContentPane().add(Cancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 620, -1, -1));

        Aceptar.setBackground(new java.awt.Color(0, 0, 51));
        Aceptar.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        Aceptar.setForeground(new java.awt.Color(255, 255, 255));
        Aceptar.setText("Aceptar");
        Aceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AceptarActionPerformed(evt);
            }
        });
        getContentPane().add(Aceptar, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 620, -1, -1));

        PanelDatosPersonales1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "            ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12), new java.awt.Color(255, 255, 255))); // NOI18N
        PanelDatosPersonales1.setOpaque(false);

        JlabelEmail8.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        JlabelEmail8.setForeground(new java.awt.Color(255, 255, 255));
        JlabelEmail8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/dni.png"))); // NOI18N

        Dni.setBackground(new java.awt.Color(0, 0, 51));
        Dni.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Dni.setForeground(new java.awt.Color(255, 255, 255));
        try {
            Dni.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("########-U")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        Dni.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Dni.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        Dni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DniActionPerformed(evt);
            }
        });

        JlabelEmail9.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        JlabelEmail9.setForeground(new java.awt.Color(255, 255, 255));
        JlabelEmail9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/email.png"))); // NOI18N

        JlabelEmail10.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        JlabelEmail10.setForeground(new java.awt.Color(255, 255, 255));
        JlabelEmail10.setText("E-mail:");

        NombreEmail.setBackground(new java.awt.Color(0, 0, 51));
        NombreEmail.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        NombreEmail.setForeground(new java.awt.Color(255, 255, 255));
        NombreEmail.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        JlabelEmail11.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        JlabelEmail11.setForeground(new java.awt.Color(255, 255, 255));
        JlabelEmail11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/sexo.png"))); // NOI18N

        JlabelSexo1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        JlabelSexo1.setForeground(new java.awt.Color(255, 255, 255));
        JlabelSexo1.setText("Sexo:");

        Hombre.setBackground(new java.awt.Color(0, 0, 51));
        grupoSexo.add(Hombre);
        Hombre.setText("Varón");
        Hombre.setToolTipText("");
        Hombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HombreActionPerformed(evt);
            }
        });

        Mujer.setBackground(new java.awt.Color(0, 0, 51));
        grupoSexo.add(Mujer);
        Mujer.setText("Mujer");
        Mujer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MujerActionPerformed(evt);
            }
        });

        JlabelDni2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        JlabelDni2.setForeground(new java.awt.Color(255, 255, 255));
        JlabelDni2.setText("D.N.I:");

        NoSabe.setBackground(new java.awt.Color(0, 0, 51));
        grupoSexo.add(NoSabe);
        NoSabe.setSelected(true);
        NoSabe.setText("N/S");
        NoSabe.setToolTipText("No sabe, no contesta");
        NoSabe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NoSabeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelDatosPersonales1Layout = new javax.swing.GroupLayout(PanelDatosPersonales1);
        PanelDatosPersonales1.setLayout(PanelDatosPersonales1Layout);
        PanelDatosPersonales1Layout.setHorizontalGroup(
            PanelDatosPersonales1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelDatosPersonales1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelDatosPersonales1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelDatosPersonales1Layout.createSequentialGroup()
                        .addComponent(JlabelEmail8)
                        .addGap(18, 18, 18)
                        .addComponent(JlabelDni2))
                    .addGroup(PanelDatosPersonales1Layout.createSequentialGroup()
                        .addComponent(JlabelEmail9)
                        .addGap(10, 10, 10)
                        .addComponent(JlabelEmail10))
                    .addGroup(PanelDatosPersonales1Layout.createSequentialGroup()
                        .addComponent(JlabelEmail11)
                        .addGap(10, 10, 10)
                        .addComponent(JlabelSexo1)))
                .addGap(18, 18, 18)
                .addGroup(PanelDatosPersonales1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(NombreEmail, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelDatosPersonales1Layout.createSequentialGroup()
                        .addComponent(NoSabe)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Hombre)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Mujer))
                    .addComponent(Dni))
                .addContainerGap(9, Short.MAX_VALUE))
        );
        PanelDatosPersonales1Layout.setVerticalGroup(
            PanelDatosPersonales1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelDatosPersonales1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelDatosPersonales1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelDatosPersonales1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Dni, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(JlabelDni2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(JlabelEmail8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelDatosPersonales1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(JlabelEmail9)
                    .addComponent(JlabelEmail10, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NombreEmail, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelDatosPersonales1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(PanelDatosPersonales1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Hombre)
                        .addComponent(Mujer)
                        .addComponent(NoSabe))
                    .addGroup(PanelDatosPersonales1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(JlabelEmail11)
                        .addComponent(JlabelSexo1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        getContentPane().add(PanelDatosPersonales1, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 70, 340, 160));

        PanelBanco1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "            ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12), new java.awt.Color(255, 255, 255))); // NOI18N
        PanelBanco1.setOpaque(false);

        NumeroCuentaCorriente.setBackground(new java.awt.Color(0, 0, 51));
        NumeroCuentaCorriente.setForeground(new java.awt.Color(255, 255, 255));
        try {
            NumeroCuentaCorriente.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("#### #### ## ##########")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        NumeroCuentaCorriente.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        NumeroCuentaCorriente.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        jLMostrarIBAN1.setForeground(new java.awt.Color(255, 255, 255));

        JlabelEmail14.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        JlabelEmail14.setForeground(new java.awt.Color(255, 255, 255));
        JlabelEmail14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/EdificioBanco.png"))); // NOI18N

        JlabelDni3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        JlabelDni3.setForeground(new java.awt.Color(255, 255, 255));
        JlabelDni3.setText("NÚMERO CUENTA CORRIENTE");

        Iban.setEditable(false);
        Iban.setBackground(new java.awt.Color(102, 102, 102));
        Iban.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        Iban.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout PanelBanco1Layout = new javax.swing.GroupLayout(PanelBanco1);
        PanelBanco1.setLayout(PanelBanco1Layout);
        PanelBanco1Layout.setHorizontalGroup(
            PanelBanco1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelBanco1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(JlabelEmail14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JlabelDni3)
                .addContainerGap())
            .addGroup(PanelBanco1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(PanelBanco1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelBanco1Layout.createSequentialGroup()
                        .addComponent(Iban, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(NumeroCuentaCorriente, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLMostrarIBAN1, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );
        PanelBanco1Layout.setVerticalGroup(
            PanelBanco1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelBanco1Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(PanelBanco1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(JlabelEmail14)
                    .addComponent(JlabelDni3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelBanco1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Iban, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NumeroCuentaCorriente, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLMostrarIBAN1, javax.swing.GroupLayout.DEFAULT_SIZE, 16, Short.MAX_VALUE)
                .addGap(18, 18, 18))
        );

        getContentPane().add(PanelBanco1, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 240, 340, 150));

        ImagenNombre1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        ImagenNombre1.setForeground(new java.awt.Color(255, 255, 255));
        ImagenNombre1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/nombre.png"))); // NOI18N
        getContentPane().add(ImagenNombre1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, 30));

        ImagenSeguridad1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        ImagenSeguridad1.setForeground(new java.awt.Color(255, 255, 255));
        ImagenSeguridad1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/privado.png"))); // NOI18N
        getContentPane().add(ImagenSeguridad1, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 60, -1, -1));

        ImagenDomicilio1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        ImagenDomicilio1.setForeground(new java.awt.Color(255, 255, 255));
        ImagenDomicilio1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/casa.png"))); // NOI18N
        getContentPane().add(ImagenDomicilio1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, -1, -1));

        ImagenDinero1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        ImagenDinero1.setForeground(new java.awt.Color(255, 255, 255));
        ImagenDinero1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/banco.png"))); // NOI18N
        getContentPane().add(ImagenDinero1, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 240, -1, -1));

        PanelDomicilio2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "            ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12), new java.awt.Color(255, 255, 255))); // NOI18N
        PanelDomicilio2.setOpaque(false);

        TipoVia.setBackground(new java.awt.Color(0, 0, 51));
        TipoVia.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        TipoVia.setForeground(new java.awt.Color(255, 255, 255));
        TipoVia.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ACCE", "ACCES", "ACEQ", "ACERA", "ALAM", "ALDAP", "ALDEA", "ALQUE", "ALTO", "ANDAD", "ANGTA", "APDRO", "APDRO", "APTES", "APTOS", "ARB", "ARRAL", "ARREK", "ARRY", "ASSEG", "ATAJO", "ATAL", "ATALL", "ATZUC", "AUTO", "AUTOV", "AUZO", "AUZOT", "AUZUN", "AV", "AVDA", "AVGDA", "AVIA", "BALNR", "BARDA", "BARRI", "BARRO", "BDA", "BELNA", "BIDE", "BIDEB", "BJADA", "BLOC", "BLQUE", "BQLLO", "BRANC", "BRDLA", "BRZAL", "BSRIA", "BULEV", "BV", "C", "C.H.", "C.N.", "C.V.", "C/", "CÑADA", "CALLE", "CAMI", "CAMIN", "CAMPA", "CAMPG", "CAMPO", "CANÑO", "CANAL", "CANT", "CANTI", "CANTO", "CANTR", "CARRA", "CARRE", "CARRY", "CASA", "CBTIZ", "CCVCN", "CELLA", "CERRO", "CHLET", "CINT", "CINT", "CINY", "CIRCU", "CJLA", "CJTO", "CLEYA", "CLLJA", "CLLON", "CLLZO", "CLYON", "CMÑO", "CMNET", "CMNO", "CNLLA", "CNVT", "CNVTO", "COL", "COMPJ", "COOP", "COSTA", "COSTE", "CRA", "CRCRO", "CRLLO", "CRO", "CRRAL", "CRRAL", "CRRCI", "CRRDA", "CRRDE", "CRRDO", "CRRIL", "CRRLO", "CRROL", "CRROL", "CRTIL", "CRTJO", "CSRIO", "CSTAN", "CTRA", "CTRIN", "CUADR", "CUEVA", "CUSTA", "CXON", "CZADA", "CZADS", "DEMAR", "DEMAR", "DHSA", "DISEM", "DISSE", "DRERA", "EDIFC", "EIRAD", "EMPR", "ENTR", "EPTZA", "ERREB", "ERREP", "ERRIB", "ESC", "ESCA", "ESCAL", "ESLDA", "ESPIG", "ESTAC", "ESTCN", "ESTDA", "ETDEA", "ETXAD", "ETXAR", "EXPLA", "EXTRM", "EXTRR", "FALDA", "FBRCA", "FINCA", "G.V.", "GAIN", "GALE", "GLLZO", "GORAB", "GRANJ", "GRUP", "GRUPO", "GTA", "HEGI", "HIPOD", "HIRIB", "HONDA", "HOYA", "ILLA", "INDA", "JARD", "JDIN", "JDIN", "JDINS", "KAI", "KALE", "KARIK", "KARRE", "KARRI", "KOSTA", "KRRIL", "LAGO", "LASTE", "LDERA", "LEKU", "LLNRA", "LLOC", "LOMA", "LOMO", "LORAK", "LORAT", "LUGAR", "MALEC", "MASIA", "MAZO", "MENDI", "MERC", "MERCT", "MIRAD", "MOLL", "MONTE", "MRDOR", "MTRIO", "MUELL", "NAVE", "NCLEO", "NUDO", "ONDA", "PAGO", "PALAC", "PANT", "PARC", "PARKE", "PARTI", "PAS", "PASAI", "PASEA", "PASEA", "PASEO", "PASEO", "PASSE", "PATIO", "PBDO", "PBLO", "PDA", "PDIS", "PG", "PGIND", "PINAR", "PISTA", "PJDA", "PL", "PLA", "PLAÇA", "PLAYA", "PLAZA", "PLCET", "PLLOP", "PLZLA", "PNTE", "POLIG", "PONT", "PONTE", "PORT", "PQUE", "PRAÑA", "PRAGE", "PRAIA", "PRAJE", "PRAXE", "PRAZA", "PROL", "PROL", "PRTAL", "PRTCO", "PRZLA", "PSAJE", "PSAXE", "PSLLO", "PSMAR", "PTA", "PTDA", "PTGE", "PTILO", "PTLLO", "PTO", "PZO", "PZTA", "RABAL", "RACDA", "RACO", "RAMAL", "RAMPA", "RAMPA", "RAVAL", "RBLA", "RBRA", "RCDA", "RCON", "RENTO", "RESID", "RIERA", "RONDA", "RTDA", "RUA", "RUELA", "RUERO", "SANAT", "SANTU", "SARBI", "SBIDA", "SECT", "SEDER", "SEDRA", "SEKT", "SEND", "SENDA", "SVTIA", "TALDE", "TOKI", "TRANS", "TRAS", "TRAS", "TRAV", "TRRNT", "TRSSI", "TRVA", "TRVAL", "URB", "URBAT", "URBAZ", "VALLE", "VCTE", "VCTO", "VECIN", "VEGA", "VENAT", "VENLA", "VIA", "VIAL", "VIANY", "VILLA", "VREDA", "VVDAS", "XDIN", "ZEHAR", "ZONA", "ZUBI", "ZUHAI", "ZUMAR" }));
        TipoVia.setToolTipText("");

        DireccionVivienda.setBackground(new java.awt.Color(0, 0, 51));
        DireccionVivienda.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        DireccionVivienda.setForeground(new java.awt.Color(255, 255, 255));
        DireccionVivienda.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        JlabelAnnos11.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        JlabelAnnos11.setForeground(new java.awt.Color(255, 255, 255));
        JlabelAnnos11.setText("Nº");

        numeroDireccion.setBackground(new java.awt.Color(0, 0, 51));
        numeroDireccion.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        numeroDireccion.setForeground(new java.awt.Color(255, 255, 255));
        numeroDireccion.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        numeroDireccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                numeroDireccionActionPerformed(evt);
            }
        });
        numeroDireccion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                numeroDireccionnumeroCasa(evt);
            }
        });

        JlabelAnnos12.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        JlabelAnnos12.setForeground(new java.awt.Color(255, 255, 255));
        JlabelAnnos12.setText("Piso");

        numeroPiso.setBackground(new java.awt.Color(0, 0, 51));
        numeroPiso.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        numeroPiso.setForeground(new java.awt.Color(255, 255, 255));
        numeroPiso.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        numeroPiso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                numeroPisoActionPerformed(evt);
            }
        });
        numeroPiso.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                numeroPiso(evt);
            }
        });

        JlabelAnnos13.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        JlabelAnnos13.setForeground(new java.awt.Color(255, 255, 255));
        JlabelAnnos13.setText("Puerta");

        letraPuerta.setBackground(new java.awt.Color(0, 0, 51));
        letraPuerta.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        letraPuerta.setForeground(new java.awt.Color(255, 255, 255));
        letraPuerta.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        letraPuerta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                letraPuertaActionPerformed(evt);
            }
        });
        letraPuerta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                letraPuertaombrePuerta(evt);
            }
        });

        JlabelAnnos14.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        JlabelAnnos14.setForeground(new java.awt.Color(255, 255, 255));
        JlabelAnnos14.setText("C.P");

        CodigoPostal.setBackground(new java.awt.Color(0, 0, 51));
        CodigoPostal.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        CodigoPostal.setForeground(new java.awt.Color(255, 255, 255));
        try {
            CodigoPostal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("#####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        CodigoPostal.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        CodigoPostal.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        mostrarProvincia.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        mostrarProvincia.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout PanelDomicilio2Layout = new javax.swing.GroupLayout(PanelDomicilio2);
        PanelDomicilio2.setLayout(PanelDomicilio2Layout);
        PanelDomicilio2Layout.setHorizontalGroup(
            PanelDomicilio2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelDomicilio2Layout.createSequentialGroup()
                .addGroup(PanelDomicilio2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelDomicilio2Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(PanelDomicilio2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelDomicilio2Layout.createSequentialGroup()
                                .addComponent(JlabelAnnos14)
                                .addGap(10, 10, 10)
                                .addComponent(CodigoPostal, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(mostrarProvincia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(PanelDomicilio2Layout.createSequentialGroup()
                                .addComponent(JlabelAnnos11)
                                .addGap(18, 18, 18)
                                .addComponent(numeroDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                                .addComponent(JlabelAnnos12, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(numeroPiso, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(24, 24, 24)
                                .addComponent(JlabelAnnos13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(letraPuerta, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(PanelDomicilio2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(TipoVia, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(DireccionVivienda)))
                .addContainerGap())
        );
        PanelDomicilio2Layout.setVerticalGroup(
            PanelDomicilio2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelDomicilio2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelDomicilio2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TipoVia, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DireccionVivienda, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelDomicilio2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(JlabelAnnos11, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numeroDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(PanelDomicilio2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(letraPuerta, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(JlabelAnnos13, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(numeroPiso, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(JlabelAnnos12, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelDomicilio2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(JlabelAnnos14, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CodigoPostal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mostrarProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        getContentPane().add(PanelDomicilio2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, -1, 150));

        textoMensajeERROR.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        getContentPane().add(textoMensajeERROR, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 450, 340, 170));

        DatosNombre.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "            ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12), new java.awt.Color(255, 255, 255))); // NOI18N
        DatosNombre.setOpaque(false);

        jLabelNombre3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabelNombre3.setForeground(new java.awt.Color(255, 255, 255));
        jLabelNombre3.setText("Nombre:");

        JlabelPrimerApellido3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        JlabelPrimerApellido3.setForeground(new java.awt.Color(255, 255, 255));
        JlabelPrimerApellido3.setText("Primer Apellido:");

        jLabelpSegundoApellido2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabelpSegundoApellido2.setForeground(new java.awt.Color(255, 255, 255));
        jLabelpSegundoApellido2.setText("Segundo Apellido:");

        años2.setFont(new java.awt.Font("Tahoma", 1, 17)); // NOI18N
        años2.setForeground(new java.awt.Color(255, 255, 255));
        años2.setText("años");

        Nombre.setBackground(new java.awt.Color(0, 0, 51));
        Nombre.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        Nombre.setForeground(new java.awt.Color(255, 255, 255));
        Nombre.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Nombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NombreActionPerformed(evt);
            }
        });
        Nombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                comprobarNombre(evt);
            }
        });

        PrimerApellido.setBackground(new java.awt.Color(0, 0, 51));
        PrimerApellido.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        PrimerApellido.setForeground(new java.awt.Color(255, 255, 255));
        PrimerApellido.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        PrimerApellido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                comprobarPrimerApellido(evt);
            }
        });

        SegundoApellido.setBackground(new java.awt.Color(0, 0, 51));
        SegundoApellido.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        SegundoApellido.setForeground(new java.awt.Color(255, 255, 255));
        SegundoApellido.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        SegundoApellido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                SegundoApellidoKeyTyped(evt);
            }
        });

        jLabelNombre4.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabelNombre4.setForeground(new java.awt.Color(255, 255, 255));
        jLabelNombre4.setText("Nombre:");

        javax.swing.GroupLayout DatosNombreLayout = new javax.swing.GroupLayout(DatosNombre);
        DatosNombre.setLayout(DatosNombreLayout);
        DatosNombreLayout.setHorizontalGroup(
            DatosNombreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DatosNombreLayout.createSequentialGroup()
                .addGroup(DatosNombreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DatosNombreLayout.createSequentialGroup()
                        .addComponent(JlabelPrimerApellido3)
                        .addGap(26, 26, 26)
                        .addComponent(PrimerApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(DatosNombreLayout.createSequentialGroup()
                        .addComponent(jLabelpSegundoApellido2)
                        .addGap(8, 8, 8)
                        .addComponent(SegundoApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 371, Short.MAX_VALUE))
            .addGroup(DatosNombreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(DatosNombreLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addGroup(DatosNombreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(DatosNombreLayout.createSequentialGroup()
                            .addGroup(DatosNombreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabelNombre3)
                                .addComponent(jLabelNombre4))
                            .addGap(91, 91, 91)
                            .addComponent(Nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(DatosNombreLayout.createSequentialGroup()
                            .addGap(660, 660, 660)
                            .addComponent(años2)))
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        DatosNombreLayout.setVerticalGroup(
            DatosNombreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DatosNombreLayout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(DatosNombreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(JlabelPrimerApellido3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PrimerApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(DatosNombreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelpSegundoApellido2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SegundoApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(255, Short.MAX_VALUE))
            .addGroup(DatosNombreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(DatosNombreLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addGroup(DatosNombreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabelNombre3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelNombre4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(310, 310, 310)
                    .addComponent(años2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        getContentPane().add(DatosNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 360, 160));

        FechaNacimiento.setBackground(new java.awt.Color(0, 0, 51));
        FechaNacimiento.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        FechaNacimiento.setForeground(new java.awt.Color(255, 255, 255));
        try {
            FechaNacimiento.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        FechaNacimiento.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        FechaNacimiento.setText("dd /mm /yyyy");
        FechaNacimiento.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        FechaNacimiento.setName(""); // NOI18N
        FechaNacimiento.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                FechaNacimientoFocusLost(evt);
            }
        });
        FechaNacimiento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FechaNacimientoActionPerformed(evt);
            }
        });
        getContentPane().add(FechaNacimiento, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 410, 100, 30));

        JlabelFecha2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        JlabelFecha2.setForeground(new java.awt.Color(255, 255, 255));
        JlabelFecha2.setText("Fecha nacimiento:");
        getContentPane().add(JlabelFecha2, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 410, -1, 30));

        Edad.setEditable(false);
        Edad.setBackground(new java.awt.Color(102, 102, 102));
        Edad.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        Edad.setForeground(new java.awt.Color(255, 255, 255));
        Edad.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        Edad.setToolTipText("");
        getContentPane().add(Edad, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 410, 70, 30));

        jCalendar1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jCalendar1fechaAutomatica(evt);
            }
        });
        getContentPane().add(jCalendar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 420, 350, 200));

        Fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/fondo.jpg"))); // NOI18N
        getContentPane().add(Fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 740, 670));
    }// </editor-fold>//GEN-END:initComponents

    private void MujerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MujerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_MujerActionPerformed

    private void HombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_HombreActionPerformed

    private void DniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DniActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DniActionPerformed

    private void AceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AceptarActionPerformed
        textoMensajeERROR.setText("<html><body>");
        Nombre.setBorder(volverBordeOriginal);
        PrimerApellido.setBorder(volverBordeOriginal);
        SegundoApellido.setBorder(volverBordeOriginal);
        numeroDireccion.setBorder(volverBordeOriginal);
        numeroPiso.setBorder(volverBordeOriginal);

        if (Nombre.getText().equals("")) {
            textoMensajeERROR.setFont(negrita);
            textoMensajeERROR.setForeground(Color.white);    //Esto igual esta prohibido
            textoMensajeERROR.setText(textoMensajeERROR.getText() + "-No puedes dejar el <font color=\"red\">Nombre</font> sin rellenar<br>");
            textoMensajeERROR.setEnabled(true);
            Nombre.setBorder(bordeRojoError);

        }
        if (PrimerApellido.getText().equals("")) {
            textoMensajeERROR.setFont(negrita);
            textoMensajeERROR.setForeground(Color.white);    //Esto igual esta prohibido
            textoMensajeERROR.setText(textoMensajeERROR.getText() + "-No puedes dejar el <font color=\"red\">Primer Apellido</font> sin rellenar<br>");
            textoMensajeERROR.setEnabled(true);
            PrimerApellido.setBorder(bordeRojoError);
        }
        if (SegundoApellido.getText().equals("")) {
            textoMensajeERROR.setFont(negrita);
            textoMensajeERROR.setForeground(Color.white);    //Esto igual esta prohibido
            textoMensajeERROR.setText(textoMensajeERROR.getText() + "-No puedes dejar el <font color=\"red\">Segundo Apellido</font> sin rellenar<br>");
            SegundoApellido.setBorder(bordeRojoError);
        }
        if (numeroDireccion.getText().toString().equals("")) {
            textoMensajeERROR.setFont(negrita);
            textoMensajeERROR.setForeground(Color.white);    //Esto igual esta prohibido
            textoMensajeERROR.setText(textoMensajeERROR.getText() + "-No puedes dejar el <font color=\"red\">Nº</font> sin rellenar<br>");
            textoMensajeERROR.setEnabled(true);
            numeroDireccion.setBorder(bordeRojoError);

        }
        comprobarCodigoPostal();
        comprobarDNI();
        validarEmail();

        comprobarCuentaCorriente();
        fechaIntroducida();
        textoMensajeERROR.setText(textoMensajeERROR.getText() + "</body></html>");
        //Si todo ha ido bien y no hay errores, guardo los datos
        if (textoMensajeERROR.getText().equals("<html><body></body></html>")) {
            try {
                imrpimirTxt();
            } catch (IOException ex) {
                Logger.getLogger(DatoInscripcion.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {

            //Icon m = new ImageIcon(getClass().getResource("Icons/error1.png"));
            JOptionPane.showMessageDialog(null, "Parece que tiene datos incorrectos, fijate en los cuadros \n rojos.", "Oops!!Algo salió mal...", JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_AceptarActionPerformed

    private void numeroDireccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_numeroDireccionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_numeroDireccionActionPerformed

    private void numeroDireccionnumeroCasa(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_numeroDireccionnumeroCasa
        char caracter;
        caracter = evt.getKeyChar();
        if (!Character.isDigit(caracter) && caracter != KeyEvent.VK_BACK_SPACE) {
            evt.consume();
            getToolkit().beep();
        }
    }//GEN-LAST:event_numeroDireccionnumeroCasa

    private void numeroPisoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_numeroPisoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_numeroPisoActionPerformed

    private void numeroPiso(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_numeroPiso
        char caracter;
        caracter = evt.getKeyChar();
        if (!Character.isDigit(caracter) && caracter != KeyEvent.VK_BACK_SPACE) {
            evt.consume();
            getToolkit().beep();
        }
    }//GEN-LAST:event_numeroPiso

    private void letraPuertaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_letraPuertaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_letraPuertaActionPerformed

    private void letraPuertaombrePuerta(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_letraPuertaombrePuerta
        char caracter;
        caracter = evt.getKeyChar();
        if (!Character.isLetter(caracter) && caracter != KeyEvent.VK_SPACE) {
            evt.consume();
            getToolkit().beep();
        }
    }//GEN-LAST:event_letraPuertaombrePuerta

    private void NombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NombreActionPerformed

    }//GEN-LAST:event_NombreActionPerformed

    private void comprobarNombre(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_comprobarNombre
        char caracter;
        caracter = evt.getKeyChar();
        if (!Character.isLetter(caracter) && caracter != KeyEvent.VK_SPACE && caracter != KeyEvent.VK_BACK_SPACE) {
            evt.consume();
            getToolkit().beep();
        }
    }//GEN-LAST:event_comprobarNombre

    private void comprobarPrimerApellido(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_comprobarPrimerApellido
        char caracter;
        caracter = evt.getKeyChar();
        if (!Character.isLetter(caracter) && caracter != KeyEvent.VK_SPACE && caracter != KeyEvent.VK_BACK_SPACE) {
            evt.consume();
            getToolkit().beep();
        }
    }//GEN-LAST:event_comprobarPrimerApellido

    private void FechaNacimientoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_FechaNacimientoFocusLost
        fechaIntroducida();
    }//GEN-LAST:event_FechaNacimientoFocusLost

    private void FechaNacimientoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FechaNacimientoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_FechaNacimientoActionPerformed

    private void CancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelarActionPerformed
        Nombre.setText("");
        Nombre.setBorder(volverBordeOriginal);

        PrimerApellido.setText("");
        PrimerApellido.setBorder(volverBordeOriginal);

        SegundoApellido.setText("");
        SegundoApellido.setBorder(volverBordeOriginal);

        DireccionVivienda.setText("");
        DireccionVivienda.setBorder(volverBordeOriginal);

        numeroDireccion.setText("");
        numeroDireccion.setBorder(volverBordeOriginal);

        FechaNacimiento.setText("");
        FechaNacimiento.setBorder(volverBordeOriginal);

        textoMensajeERROR.setText("<html><body>");

        numeroPiso.setText("");
        numeroPiso.setBorder(volverBordeOriginal);

        letraPuerta.setText("");
        letraPuerta.setBorder(volverBordeOriginal);

        CodigoPostal.setText("");
        CodigoPostal.setBorder(volverBordeOriginal);

        mostrarProvincia.setText("");

        Iban.setText("");
        NumeroCuentaCorriente.setText("");
        NumeroCuentaCorriente.setBorder(volverBordeOriginal);

        Dni.setText("");
        Dni.setBorder(volverBordeOriginal);

        NombreEmail.setText("");
        NombreEmail.setBorder(volverBordeOriginal);

        Edad.setText("");

        NoSabe.setSelected(true);
    }//GEN-LAST:event_CancelarActionPerformed

    private void jCalendar1fechaAutomatica(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jCalendar1fechaAutomatica
        Date fecha = jCalendar1.getDate();
        String dia = String.valueOf(fecha.getDate());
        String mes = String.valueOf(fecha.getMonth() + 1);
        String anyo = String.valueOf(fecha.getYear() + 1900);
        if (mes.length() == 1) {
            mes = "0" + mes;
        }
        if (dia.length() == 1) {
            dia = "0" + dia;
        }
        FechaNacimiento.setText(dia + mes + anyo);
    }//GEN-LAST:event_jCalendar1fechaAutomatica

    private void NoSabeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NoSabeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NoSabeActionPerformed

    private void SegundoApellidoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SegundoApellidoKeyTyped
        char caracter;
        caracter = evt.getKeyChar();
        if (!Character.isLetter(caracter) && caracter != KeyEvent.VK_SPACE && caracter != KeyEvent.VK_BACK_SPACE) {
            evt.consume();
            getToolkit().beep();
        }
    }

    public static void main(String args[]) {


        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DatoInscripcion().setVisible(true);
            }
        });

    }//GEN-LAST:event_SegundoApellidoKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Aceptar;
    private javax.swing.JButton Cancelar;
    private javax.swing.JFormattedTextField CodigoPostal;
    private javax.swing.JPanel DatosNombre;
    private javax.swing.JTextField DireccionVivienda;
    private javax.swing.JFormattedTextField Dni;
    private javax.swing.JTextField Edad;
    private javax.swing.JFormattedTextField FechaNacimiento;
    private javax.swing.JLabel Fondo;
    private javax.swing.JRadioButton Hombre;
    private javax.swing.JTextField Iban;
    private javax.swing.JLabel ImagenDinero1;
    private javax.swing.JLabel ImagenDomicilio1;
    private javax.swing.JLabel ImagenNombre1;
    private javax.swing.JLabel ImagenSeguridad1;
    private javax.swing.JLabel JlabelAnnos11;
    private javax.swing.JLabel JlabelAnnos12;
    private javax.swing.JLabel JlabelAnnos13;
    private javax.swing.JLabel JlabelAnnos14;
    private javax.swing.JLabel JlabelDni2;
    private javax.swing.JLabel JlabelDni3;
    private javax.swing.JLabel JlabelEmail10;
    private javax.swing.JLabel JlabelEmail11;
    private javax.swing.JLabel JlabelEmail14;
    private javax.swing.JLabel JlabelEmail8;
    private javax.swing.JLabel JlabelEmail9;
    private javax.swing.JLabel JlabelFecha2;
    private javax.swing.JLabel JlabelPrimerApellido3;
    private javax.swing.JLabel JlabelSexo1;
    private javax.swing.JRadioButton Mujer;
    private javax.swing.JRadioButton NoSabe;
    private javax.swing.JTextField Nombre;
    private javax.swing.JTextField NombreEmail;
    private javax.swing.JFormattedTextField NumeroCuentaCorriente;
    private javax.swing.JPanel PanelBanco1;
    private javax.swing.JPanel PanelDatosPersonales1;
    private javax.swing.JPanel PanelDomicilio2;
    private javax.swing.JTextField PrimerApellido;
    private javax.swing.JTextField SegundoApellido;
    private javax.swing.JComboBox<String> TipoVia;
    private javax.swing.JLabel años2;
    private javax.swing.ButtonGroup grupoSexo;
    private com.toedter.calendar.JCalendar jCalendar1;
    private javax.swing.JLabel jLMostrarIBAN1;
    private javax.swing.JLabel jLabelNombre3;
    private javax.swing.JLabel jLabelNombre4;
    private javax.swing.JLabel jLabelTitulo;
    private javax.swing.JLabel jLabelpSegundoApellido2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JFormattedTextField letraPuerta;
    private javax.swing.JLabel mostrarProvincia;
    private javax.swing.JFormattedTextField numeroDireccion;
    private javax.swing.JFormattedTextField numeroPiso;
    private javax.swing.JLabel textoMensajeERROR;
    // End of variables declaration//GEN-END:variables
}
