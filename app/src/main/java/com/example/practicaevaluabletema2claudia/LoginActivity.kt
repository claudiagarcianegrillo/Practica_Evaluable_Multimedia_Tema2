package com.example.practicaevaluabletema2claudia
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.practicaevaluabletema2claudia.PrincipalActivity
import com.example.practicaevaluabletema2claudia.R
import com.example.practicaevaluabletema2claudia.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    // Constantes con el usuario y la contraseña correctos
    companion object {
        private const val MYUSER = "admin"
        private const val MYPASS = "1234"
    }

    // Nos da acceso a todas las vistas del layout activity_login.xml
    // lateinit para que la variable se inicialice más tarde (en el onCreate)
    private lateinit var bindingMain: ActivityLoginBinding

    // Inflamos y mostramos el diseño del activity_login.xml
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Convierte el xml del login en objetos en memoria y así accedemos a sus vistas
        bindingMain = ActivityLoginBinding.inflate(layoutInflater)

        // Cargamos el diseño que hicimos en el xml del login
        setContentView(bindingMain.root)

        // Listener del botón, que se ejecuta al hacer clic en "Validar"
        bindingMain.botonValidar.setOnClickListener {
            // Leemos el texto que se ha introducido quitando los espacios vacíos a los lados con trim()
            val usuario = bindingMain.editUsuario.text.toString().trim()
            val password = bindingMain.editPassword.text.toString().trim()

            // Validamos las credenciales
            if (usuario == MYUSER && password == MYPASS) {
                /* Si las credenciales son correctas creamos un intent explicito
                para ir a la siguiente pantalla, es decir, a PrincipalActivity */
                val intent = Intent(this, PrincipalActivity::class.java)

                // Enviamos los datos a la siguiente activity
                intent.putExtra("usuario", usuario)
                intent.putExtra("password", password)

                // Lanzamos la pantalla principal
                startActivity(intent)

                /* Cerramos el login para que el botón "atrás" del móvil
                no vuelva a él, si no que nos tengamos que registrar de nuevo */
                finish()
            } else {
                /* Si las credenciales no coinciden se mostrará este mensaje en la pantalla
                con el aviso que tenemos en el fichero strings.xml, es decir, este error
                'Usuario o contraseña incorrectos' */
                Toast.makeText(this, getString(R.string.mensaje_error), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
