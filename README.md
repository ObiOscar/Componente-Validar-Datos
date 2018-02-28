# EditorDeTexto
Si te interesa el código, descarga el proyecto con Netbeans, si quieres el componente, se encuentra en la carpeta dist <br>
<b>DATOS SOLO COMPROBADOS PARA ESPAÑA</b><br>
En la imagen interfaceEjemplo.png se ve lo que pedía el supuesto cliente.

## EJERCICIO 1

Realiza un componente que pueda ser integrado en cualquier programa,

La disposición de los elementos es orientativa, podrá modificarse si aporta una mejora. El componente deberá ser operativo.
Se deberá controlar:
*   Fecha de nacimiento. Deberá ser lógica
*   La edad se calculará de forma automática al introducir la fecha de nacimiento. Deberá estar entre 18 y 70 años.
*   El DNI deberá ajustarse a la normativa vigente.
*   Todos los datos numéricos serán, además de numéricos, razonables.
 
## EJERCICIO 2

Partiendo del primer componente, se añadirá el número de cuenta del usuario en el diseño.

*   Se verificará que el número de cuenta es correcto.
*   Se calculará el IBAN y se pondrá en el cuadro de color azul.

----------------------------------------------------------------------------------------------
## IMAGEN DEL PROGRAMA CONSEGUIDO
![   ](https://github.com/ObiOscar/Componente-Validar-Datos/blob/master/primeraImagen.png)

Si falta algún dato, o no tiene un formato correcto...

![   ](https://github.com/ObiOscar/Componente-Validar-Datos/blob/master/datosIncorrectos.png)


## Resumen
* Valida que los campos Nombre y Primer apellido sea alfabético y no admita caracteres especiales. 
* El segundo Apellido, por aquello de los nombres raros, chinos, o árabes, cabe la posibilidad de escribir cualquier tipo de caracter (Sino le gusta al cliente simplemente es llamar al mimo método que que nombre y primer apellido).
* DNI, válida que el DNI sea correcto
* E-mail, valída que el cuerpo del e-mail sea del tipo aaaa@aaaa.aaaa, donde necesitas caracteres alfábeticos y númericos antes del @ excluyendo caráctareres especiales, es indiferente la longitud de cada tramo
* El sexo no está marcado por aquello de la disforia de genero, gente que no quiere proclamar su sexo
* La calle no es controlada, ya que en la calle puede entrar números e incluso carácteres, en el desplegable de tipo de vía, están todos tipos de vía del Ministerio de Fomento
* El número es obligatorio, al igual que el piso, sino se tiene uno de estos pues se pone un 0
* La puerta admite tanto letra como numero, ya que puedes vivir en la puerta 1A, sino tienes no hace falta rellenarla ya que considero que la puerta es posible que no tengas
* El código postal revisa si el código existe, comprobando primero de que provincia es y si los otros 3 números están en los barémos que le pertenece a esa provincia, imprime la provincia.
* El número de cuenta corriente comprueba si la cuenta corriente existe, si es todo 0, esta cuenta no existe, pero es un pequeño "hack" que tiene usando el método oficial para validar la cuenta corriente. Nos devuelve en la casilla de al lado el número de IBAN.
* La fecha, es posible que la escribas usando el Jcalendar que hay al lado o escribiendola a mano, de cualquier modo llama al mismo método el cual se ejecuta sin necesidar de pulsar aceptar, pero si pulsar en otra casilla.
  La fecha nos arroja 3 tipos de errores, si la fecha es futura, la fecha es menor de 18 años, la fecha es mayor de 70 años

En todos los campos se distingue entre el error de campo vacio, y el de campo mal rellenado.

Si pulsas Aceptar y hay datos incorrectos, aparecen los errores en un Jlabel oculto, que se rellena y da formato usando HTML, si vas corrigiendo tus errores, los errores en este Jlabel van desapareciendo. Sale un JoptionPanel diciendo que hay problemas

<strong>Si pulsas Aceptar y todos los datos son correctos, estos son guardados en un txt, llamado "datosFormulario.txt" si otro cliente se regista lo guarda seguido de este sin pisar los datos anteriores, si el cliente en lugar de un txt prefiere guardarlo en un xml o en una base de datos no habría problema. </strong>
Se creará un archivo con formato txt llamado <b>datosFormulario.txt</b> en el lugar donde tengamos el proyecto java o el componente

## Autor

[Oscar Fernández Rodriguez](https://github.com/ObiOscar) (ObiOscar)

## Licencia

![](https://github.com/ObiOscar/BombaAirsoft/blob/master/licencia.png)  
[Creative Commons Attribution-ShareAlike 4.0 International License](http://creativecommons.org/licenses/by-sa/4.0/)

