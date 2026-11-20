package com.example.practicaevaluabletema2claudia

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.practicaevaluabletema2claudia.databinding.ActivityPrincipalBinding

class PrincipalActivity : AppCompatActivity() {

    //Declaramos la variable para el View Binding
    private lateinit var binding: ActivityPrincipalBinding

    //
    private var usuario: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Inflamos el layout usando View Binding
        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Recuperamos los datos enviados desde el login a través de un intent
        usuario = intent.getStringExtra("usuario")

        //Configuramos los botones de navegación usando binding
        binding.btnChistes.setOnClickListener {
            val intent = Intent(this, ChistesActivity::class.java)
            startActivity(intent)
        }

        binding.btnConfiguracion.setOnClickListener {
            val intent = Intent(this, ConfiguracionActivity::class.java)
            startActivity(intent)
        }

        binding.btnDados.setOnClickListener {
            val intent = Intent(this, DadosActivity::class.java)
            startActivity(intent)
        }

        // El botón de desconectarse vuelve al login
        binding.btnDesconectar.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        //Abrimos las preferencias donde el activity ConfiguracionActivity guarda el los datos, en este caso vamos a usar el nombre
        val prefs = getSharedPreferences("config", MODE_PRIVATE)

        //Leemos el nombre guardado en la configuración
        val nombre = prefs.getString("nombre", "")

        //Si hay un nombre guardado desde la configuracion ponemos ese
        if (!nombre.isNullOrEmpty()) {
            binding.textoDatos.text = getString(R.string.saludo, nombre)
        } else if (!usuario.isNullOrEmpty()) {
            //Si no hay nombre pero si hay usuario del login, saludamos con el usuario, es decir , con 'admin'
            binding.textoDatos.text = getString(R.string.saludo, usuario)
        } else {
            //Si no hay ninguno de los dos mostramos un mensaje por defecto
            binding.textoDatos.text = getString(R.string.bienvenida_defecto)
        }
    }
}