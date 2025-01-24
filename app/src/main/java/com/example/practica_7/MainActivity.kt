package com.example.practica_7

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var adapter: TaskAdapter
    private var lista : MutableList<Task> = mutableListOf()
    private lateinit var taskDao: TaskDao
    private lateinit var recyclerView : RecyclerView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        taskDao = (application as TaskApp).room.taskDao()
        val addTask : FloatingActionButton = findViewById(R.id.addTask)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@MainActivity)

        loadTasks()

        addTask.setOnClickListener {
            startActivity(Intent(this, AddTask::class.java))
        }
    }

    private fun openTaskDetails(task: Task) {
        val intent = Intent(this, AddTask::class.java).apply {
            putExtra("IS_EDIT", true)
            putExtra("taskId", task.id)
            putExtra("taskName", task.nombre)
            putExtra("taskDesc", task.descripcion)
            putExtra("taskDate", task.fecha)
            putExtra("taskState", task.estado)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }

    private fun loadTasks() {
        CoroutineScope(Dispatchers.IO).launch {
            val tasks = taskDao.getAllTasks()
            lista = tasks.toMutableList()

            runOnUiThread {
                adapter = TaskAdapter(lista, onTaskDeleted = {
                    task ->
                    lista.remove(task)
                    adapter.notifyDataSetChanged()
                }, onTaskSelected = {
                    task ->
                    openTaskDetails(task)
                })
                recyclerView.adapter = adapter
            }
        }
    }

//    override fun onStop() {
//        super.onStop()
//        saveData()
//    }

//    fun saveData() {
//        val gson = Gson()
//        val json = gson.toJson(lista)
//        val sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        editor.putString("task", json)
//        editor.apply()
//    }
//
//    fun loadData(sharedPreferences: SharedPreferences): MutableList<Task> {
//        val gson = Gson()
//        val json = sharedPreferences.getString("task", null)
//        val type = object : TypeToken<MutableList<Task>>() {}.type
//        return  if (!json.isNullOrEmpty()) {
//            try {
//                gson.fromJson(json, type)
//            } catch (e: Exception) {
//                mutableListOf()
//            }
//        }else{
//            mutableListOf()
//        }
//    }
}