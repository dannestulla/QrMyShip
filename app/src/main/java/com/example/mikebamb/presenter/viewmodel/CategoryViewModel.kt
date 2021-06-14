package com.example.mikebamb.presenter.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mikebamb.data.EquipmentsRepository
import com.example.mikebamb.data.local.EquipmentEntity
import com.example.mikebamb.domain.EquipmentUseCase
import com.example.mikebamb.presenter.adapter.CategoryAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class CategoryViewModel @Inject constructor(
    val app: Application,
    repository: EquipmentsRepository

) : ViewModel() {
    var exitCategoryMenu = false
    var checkedRemote = false
    lateinit var categorySelected: List<String>
    lateinit var remoteDBdata: MutableCollection<Any>
    var mAdapter = CategoryAdapter()
    val equipmentUseCase = EquipmentUseCase(repository)
    var listFromDB = ArrayList<EquipmentEntity>()
    var recyclerViewItems = MutableLiveData<List<EquipmentEntity>>()
    var currentCategory = MutableLiveData<List<String>>()

    fun getMainCategory() {
        CoroutineScope(IO).launch {
            val mainCategoryList = equipmentUseCase.getMainCategory().distinct()
            if (mainCategoryList.isNullOrEmpty()) {
                Log.e("Info", "Your Local Database is Empty!")
            } else {
                currentCategory.postValue(mainCategoryList)
                categorySelected = mainCategoryList
            }
        }
    }

    fun getSubCategory(subCategory: String) {
        CoroutineScope(IO).launch {
            val search = equipmentUseCase.getSubCategory(subCategory)
            currentCategory.postValue(search)
            postSubCategory(search)
        }
    }
  /*  fun getSubSubCategory(subSubCategory: String) {
        CoroutineScope(IO).launch {
            val search2 = equipmentUseCase.getSubSubCategory(subSubCategory).distinct()
            currentCategory.postValue(search2)
            postSubCategory(search2)
        }
    }*/

    private fun postSubCategory(subCategoryList: List<String>) {
        currentCategory.postValue(ArrayList())
        currentCategory.postValue(subCategoryList)
        categorySelected = subCategoryList
        exitCategoryMenu = true
    }

    fun initializeRemoteDatabase() {
        equipmentUseCase.initializeRemoteDatabase()
    }

    fun getListFromDatabase() {
        CoroutineScope(IO).launch {
            listFromDB = equipmentUseCase.getEquipmentsFromDatabase() as ArrayList<EquipmentEntity>
            recyclerViewItems.postValue(listFromDB)
        }
    }

    fun getAllRemoteData() {
        equipmentUseCase.getAllRemoteData()
        checkedRemote = true
    }

    fun compareRemoteAndLocalData(remoteDBdata: MutableCollection<Any>) {
        equipmentUseCase.compareRemoteAndLocalData(remoteDBdata)
    }
}

