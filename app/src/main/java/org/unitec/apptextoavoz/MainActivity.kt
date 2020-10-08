package org.unitec.apptextoavoz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.util.*
import kotlin.concurrent.schedule

//Al incio se agregan las interfaces
//El metodo que invoca la interfaz TextToSpeech.OnInitListener
class MainActivity : AppCompatActivity(),TextToSpeech.OnInitListener {
    //objeto intermediario de la app y TextToSpeech
    private var tts:TextToSpeech?=null
    //Este codigo nos ayuda a garantizar que TextToSpeech se inicio completamente
    private val CODIGO_PETICION=100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Iniciamos la variable tts para que ya no este null, es un constructor
        tts=TextToSpeech(this,this) //si L activity y el listener estan en el mismo archivo se utliza this
        //INVOCAMOS EL BOTON DE con ID hablar
        hablar.setOnClickListener{
            val intent=Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            try {
                startActivityForResult(intent,CODIGO_PETICION)
            }
            catch (e:Exception){ }
        }
        //Programar el botton con ID interpretar
        interpretar.setOnClickListener{ //boton de interpretar
            if(fraseEscrita.text.isEmpty()){ //veremos si el botton con ID esta vacio o no
                Toast.makeText(this,"no has escrito nada",Toast.LENGTH_LONG).show()
            }
            else{
                hablarTexto(fraseEscrita.text.toString())
            }
        }
        //Mensaje de bienvenida
        Timer("Bienvenida",false).schedule(1000){
            //metodo speak tiene 3 parametros 1)Lo que va a decir 2)Eliminar un msj de cola "si existe" 3)
            tts!!.speak("Hola Bienvenido a mi aplicacion", TextToSpeech.QUEUE_FLUSH,null,"")
        }
    }

    override fun onInit(estado: Int) {
        //ESTA FUNCION SE iniciliza la configuracion para arrancar la app
        if(estado == TextToSpeech.SUCCESS){
            var local=Locale("spa","MEX") //configura el idioma y el pais
            val resultado=tts!!.setLanguage(local) //!! significa que si la variable tts no se ha inicilaizado, que se espere
            if(resultado==TextToSpeech.LANG_MISSING_DATA){
                Log.i("malo","No funciono el lenguaje")
            }
        }
    }
    //FUNCION QUE NOS AYUDA A INTERPRETAR LO QUE SE ESCRIBA EN EL TEXTO DE LA FRASE
    fun hablarTexto(textohablar:String){
        tts!!.speak(textohablar,TextToSpeech.QUEUE_FLUSH,null,"")
    }
    //METODO PARA LIMPIAR LA MEMORIA AL MOMENTO DE CERRAR LA APP
    override fun onDestroy(){
        super.onDestroy()
        if(tts!=null){
            tts!!.stop()
            tts!!.shutdown()
        }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            CODIGO_PETICION->{
                if(resultCode==RESULT_OK && null != data){
                    val result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    //PEN ESTA PARTE NOS MOSTRARA EN EL TEXTOESTATICO ID textoInterpretado lo que dijimos
                    textoInterpretado.setText(result!![0])
                }
            }
    }
    }
}