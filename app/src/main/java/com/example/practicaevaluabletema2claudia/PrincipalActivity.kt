package com.example.practicaevaluabletema2claudia

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.practicaevaluabletema2claudia.databinding.ActivityPrincipalBinding

class PrincipalActivity : AppCompatActivity() {

    // Declaramos la variable para el View Binding
    private lateinit var binding: ActivityPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Inflamos el layout usando View Binding
        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Recuperamos los datos enviados desde el login a través del intent
        val usuario = intent.getStringExtra("usuario")

        // Mostramos un mensaje de bienvenida personalizado con el usuario recibido
        if (!usuario.isNullOrEmpty()) {
            binding.textoDatos.text = getString(R.string.saludo, usuario)
        } else {
            // Mensaje por defecto si no llega usuario que nunca pasará porque no te deja entrar si no esa contraseña y usuario
            binding.textoDatos.text = getString(R.string.bienvenida_defecto)
        }

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
}