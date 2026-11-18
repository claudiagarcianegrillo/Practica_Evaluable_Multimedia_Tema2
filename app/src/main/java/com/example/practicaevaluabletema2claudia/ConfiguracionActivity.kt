package com.example.practicaevaluabletema2claudia

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.practicaevaluabletema2claudia.databinding.ActivityConfiguracionBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class ConfiguracionActivity : AppCompatActivity() {

    //Para poder acceder a los elementos del XML
    private lateinit var bindingMain: ActivityConfiguracionBinding

    //Variables para guardar la fecha de nacimiento
    private var anio = 0
    private var mes = 0
    private var dia = 0
    private var hayFecha = false

    // Scope para las corrutinas (permite hacer la barra de carga en segundo plano)
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Inicializamos el View Binding y lo vinculamos con la interfaz
        bindingMain = ActivityConfiguracionBinding.inflate(layoutInflater)
        setContentView(bindingMain.root)

        //Configuramos el spinner con las opciones de tiempo entre tiradas de dados
        val opciones = listOf("1 segundo", "2 segundos", "3 segundos")
        //Asignamos el adaptador con las opciones al spinner de la interfaz
        bindingMain.spinnerTiempo.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opciones)

        //Cargamos los datos guardados previamente, si es que los hubiera
        cargarFormulario()

        //Configuramos el DatePicker para que al pulsar el botón se despliegue el calendario
        bindingMain.btnFecha.setOnClickListener {
            //Obtenemos la instancia del calendario
            val hoy = Calendar.getInstance()
            //Si el usuario ya ha elegido una fecha antes, se abre en esa, si no, en el día que estemos
            val a = if (hayFecha) anio else hoy.get(Calendar.YEAR)
            val m = if (hayFecha) mes else hoy.get(Calendar.MONTH)
            val d = if (hayFecha) dia else hoy.get(Calendar.DAY_OF_MONTH)

            //Creamos y mostramos el cuadro de diálogo del calendario
            DatePickerDialog(this, { _, year, month, day ->
                //Este código se va a ejecutar cuando el usuario selecciona una fecha y pulsa el botón Aceptar
                anio = year
                mes = month
                dia = day
                hayFecha = true
                //Mostramos la fecha seleccionada en el TextView, +1 porque los meses en Java y Kotlin empiezan en 0
                bindingMain.txtFecha.text = "$day/${month + 1}/$year"
            }, a, m, d).show()
        }

        //Este es el botón Guardar que valida y guarda toda la información
        bindingMain.btnGuardar.setOnClickListener {
            guardar()
        }

        //Este es el botón de volver al activity principal
        bindingMain.btnVolver.setOnClickListener {
            finish()
        }
    }

    /*Este metodo sirve para recuperar los datos almacenados en
    SharedPreferences y rellenar la pantalla*/
    private fun cargarFormulario() {
        val prefs = getSharedPreferences("config", MODE_PRIVATE)
        bindingMain.edtNombre.setText(prefs.getString("nombre", ""))
        bindingMain.edtMarca.setText(prefs.getString("marca", ""))
        bindingMain.chkBase.isChecked = prefs.getBoolean("base", false)
        bindingMain.chkLabial.isChecked = prefs.getBoolean("labial", false)
        bindingMain.chkSombras.isChecked = prefs.getBoolean("sombras", false)

        //Esta es la posición del Spinner que es igual a los segundos elegidos menos 1
        bindingMain.spinnerTiempo.setSelection(prefs.getInt("tiempo", 1) - 1)
        bindingMain.swVoz.isChecked = prefs.getBoolean("voz", true)

        //Seleccionamos el RadioButton correcto según el tipo de piel guardado
        when (prefs.getString("piel", "mixta")) {
            "seca" -> bindingMain.radioGroup.check(R.id.radio_seca)
            "grasa" -> bindingMain.radioGroup.check(R.id.radio_grasa)
            else -> bindingMain.radioGroup.check(R.id.radio_mixta)
        }

        //Si ya hay una fecha guardada la seleccionamos para ponerla en la interfaz
        if (prefs.getInt("anio", 0) != 0) {
            anio = prefs.getInt("anio", 0)
            mes = prefs.getInt("mes", 0)
            dia = prefs.getInt("dia", 0)
            hayFecha = true
            bindingMain.txtFecha.text = "$dia/${mes + 1}/$anio"
        }
    }

    //Este metodo sirve para validar campos,
    // guardar en SharedPreferences y simular la barra de carga con las corrutinas
    private fun guardar() {
        //Leemos el nombre y quitamos espacios sobrantes
        val nombre = bindingMain.edtNombre.text.toString().trim()

        //Hacemos que el nombre sea obligatorio, si no devuelve un mensaje de error
        if (nombre.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_nombre), Toast.LENGTH_SHORT).show()
            return
        }

        //Leemos la marca y quitamos los espacios también
        val marca = bindingMain.edtMarca.text.toString().trim()

        //Obtenemos el tipo de piel seleccionado en el RadioGroup
        val piel = when (bindingMain.radioGroup.checkedRadioButtonId) {
            R.id.radio_seca -> "seca"
            R.id.radio_grasa -> "grasa"
            else -> "mixta"
        }

        //El tiempo en segundos es la posición del spinner + 1
        val tiempo = bindingMain.spinnerTiempo.selectedItemPosition + 1

        //Guardamos todos los valores en SharedPreferences para que los lean las demás pantallas
        val editor = getSharedPreferences("config", MODE_PRIVATE).edit()
        editor.putString("nombre", nombre)
        editor.putString("marca", marca)
        editor.putString("piel", piel)
        editor.putBoolean("base", bindingMain.chkBase.isChecked)
        editor.putBoolean("labial", bindingMain.chkLabial.isChecked)
        editor.putBoolean("sombras", bindingMain.chkSombras.isChecked)
        editor.putInt("tiempo", tiempo)
        editor.putBoolean("voz", bindingMain.swVoz.isChecked)

        //Si el usuario eligió una fecha se la guardamos también
        if (hayFecha) {
            editor.putInt("anio", anio)
            editor.putInt("mes", mes)
            editor.putInt("dia", dia)
        }
        //Esto aplica los cambios de forma asíncrona en las preferencias compartidas
        editor.apply()

        //Mostramos la barra de progreso y bloqueamos el botón mientras simulamos el guardado
        bindingMain.progressGuardar.progress = 0
        bindingMain.progressGuardar.visibility = View.VISIBLE
        bindingMain.btnGuardar.isEnabled = false

        //Usamos una corrutina para animar la barra de progreso de forma más fluida
        scope.launch {
            withContext(Dispatchers.Default) {
                for (i in 1..100) {
                    delay(15) // Pausa de 15ms por cada 1%
                    withContext(Dispatchers.Main) {
                        bindingMain.progressGuardar.progress = i
                    }
                }
            }
            //Al terminar la carga se muestra un mensaje de que ya se han guardadotodos los datos y cerramos la pantalla
            Toast.makeText(this@ConfiguracionActivity, getString(R.string.config_guardada), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    //Esto se ejecuta al destruir la actividad para cancelar
    //las corrutinas activas y evitar fugas de memoria
    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}