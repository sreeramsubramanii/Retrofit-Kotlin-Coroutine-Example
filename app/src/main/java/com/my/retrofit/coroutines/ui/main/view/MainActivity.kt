package com.my.retrofit.coroutines.ui.main.view

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.my.myapplication.appDatabse.AppDatabase
import com.my.myapplication.daos.FileStorageDao
import com.my.myapplication.entity.FileStorage
import com.my.retrofit.coroutines.R
import com.my.retrofit.coroutines.data.api.ApiHelper
import com.my.retrofit.coroutines.data.api.RetrofitBuilder
import com.my.retrofit.coroutines.data.model.FileStorageEntity
import com.my.retrofit.coroutines.data.model.User
import com.my.retrofit.coroutines.ui.base.ViewModelFactory
import com.my.retrofit.coroutines.ui.main.adapter.MainAdapter
import com.my.retrofit.coroutines.ui.main.viewmodel.MainViewModel
import com.my.retrofit.coroutines.utils.Status
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


class MainActivity : AppCompatActivity() {

    private var fileStor: List<FileStorage>? = null

    private var fileStorageEntity: FileStorageEntity? = null
    private lateinit var viewmodel: MainViewModel
    private lateinit var adapter: MainAdapter
    private val ACTIVITY_CHOOSE_FILE: Int = 0
    val DOC = "application/msword"
    val DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    val IMAGE = "image/*"
    val AUDIO = "audio/*"
    val TEXT = "text/*"
    val PDF = "application/pdf"
    val XLS = "application/vnd.ms-excel"

    private var db: AppDatabase? = null
    private var fileStorageDao: FileStorageDao? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpViewModel()
        setUpUI()
        searchFileDir(Environment.getExternalStorageDirectory().absoluteFile)

        setupObservers()

//
//        button.setOnClickListener {
////            readMutipleFileFromInternalStorage()
//
//
////            val root = File(Environment.getExternalStorageDirectory().absolutePath)
////            ListDir(root)
//        }
    }

    private fun setUpViewModel() {
        viewmodel =
            ViewModelProviders.of(this, ViewModelFactory(ApiHelper(RetrofitBuilder.apiService)))
                .get(MainViewModel::class.java)
    }

    private fun setUpUI() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MainAdapter(arrayListOf())
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
    }

    private fun setupObservers() {
        viewmodel.getUsers().observe(this, Observer {
            it?.let {

                when (it.status) {

                    Status.SUCCESS -> {
                        recyclerView.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        it.data?.let { users -> retrieveList(users) }
                    }
                    Status.ERROR -> {
                        recyclerView.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        progressBar.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }

                }
            }
        })
    }

/*
* @setupObserversForCreateFileStorage
* */

    private fun setupObserversForCreateFileStorage(fileStorageEntity: FileStorageEntity) {


        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(this@MainActivity, fileStorageEntity.toString(), Toast.LENGTH_LONG)
                .show()

            viewmodel.createFileStorage(fileStorageEntity).observe(this@MainActivity, Observer {


                it?.let {

                    when (it.status) {

                        Status.SUCCESS -> {

                            Toast.makeText(
                                this@MainActivity,
                                "Api call  success",
                                Toast.LENGTH_LONG
                            )
                                .show()

                        }
                        Status.ERROR -> {

                            Toast.makeText(this@MainActivity, "Api call failed", Toast.LENGTH_LONG)
                                .show()
                        }
                        Status.LOADING -> {
                            progressBar.visibility = View.VISIBLE
                        }

                    }
                }
            })

        }

    }


    private fun retrieveList(users: List<User>) {
        adapter.apply {
            addUsers(users)
            notifyDataSetChanged()
        }

    }


    private fun searchFileDir(dir: File) {


        val pdfPattern = ".pdf"
        val docPattern = ".doc"
        val fileList = dir.listFiles()
        if (fileList != null) {
            for (i in fileList.indices) {
                if (fileList[i].isDirectory) {
                    searchFileDir(fileList[i])
                } else {
                    if (fileList[i].name.endsWith(pdfPattern) || fileList[i].name.endsWith(
                            docPattern
                        )
                    ) {

                        Observable.fromCallable {
                            db = AppDatabase.getAppDataBase(context = this)
                            fileStorageDao = db?.fileStorageDao()

                            var fileSize = db?.fileStorageDao()?.getFiles()
                            var length = fileSize?.size

                            val uri: Uri? = Uri.parse(fileList[i].absolutePath.toString())

                            var gender1 = FileStorage(

                                Filename = fileList[i].name,
                                fileType = File(fileList[i].name).extension,
                                filePath = fileList[i].absolutePath,
                                isSyncedWith = false
//
                            )

                            with(fileStorageDao) {
                                this?.insertFile(gender1)


                            }
                            db?.fileStorageDao()?.getFiles()
                        }.doOnNext { list ->

                            fileStor = list


                            fileStor?.map {
                                fileStorageEntity = FileStorageEntity(
                                    Filename = it.Filename,
                                    fileType = it.fileType,
                                    filePath = it.filePath,
                                    isSyncedWith = false
                                )
                            }

                            setupObserversForCreateFileStorage(fileStorageEntity!!)

                            Log.v("databse querires", list.toString())

                        }.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe()


                    }
                }
            }
        }
    }


    private fun readMutipleFileFromInternalStorage() {
        //Chose only doc file
        val intent: Intent = getCustomFileChooserIntent(DOC, PDF)!!
        startActivityForResult(intent, 0)
    }


    private fun getCustomFileChooserIntent(vararg types: String?): Intent? {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        // Filter to only show results that can be "opened"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        return intent
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        when (requestCode) {
            ACTIVITY_CHOOSE_FILE -> if (resultCode === Activity.RESULT_OK) {
                Log.v("pic success", data.toString())


                if (null != data) {


                    updateDateToLocalDb(data)
                    Log.v("pic success", data.data.toString())

                    Toast.makeText(this, data.toString(), Toast.LENGTH_LONG)
                } else {
                    Log.v("pic failed", "Failed to choose picture")
                }
            }

        }

    }

    private fun updateDateToLocalDb(data: Intent) {
        Observable.fromCallable {
            db = AppDatabase.getAppDataBase(context = this)
            fileStorageDao = db?.fileStorageDao()

            val uri: Uri? = data.data
            val myFile = File(uri.toString())
            val name = myFile.name
            var fileType = getMimeType(data.data)
            var gender1 = FileStorage(

                id = 1,
                Filename = name,
                fileType = fileType.toString(),
                filePath = fileType.toString(),
                isSyncedWith = false
            )

            with(fileStorageDao) {
                this?.insertFile(gender1)
            }
            db?.fileStorageDao()?.getFiles()
        }.doOnNext { list ->

            Log.v("databse querires", list.toString())

        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()


    }


    private fun getMimeType(uri: Uri?): String? {
        this.let {
            val cr: ContentResolver = it.contentResolver
            if (uri == null) return null
            return cr.getType(uri) ?: null
        }

    }


}