package com.nbehary.retribution.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(table_name="colors")
data class Colors (@ColumnInfo(name="folder_back") var folderBack: Int = 0,
                   @ColumnInfo(name="folder_labels") var folderLabel: Int = 0,
                   @ColumnInfo(name="folder_name") var folderName: Int = 0,
                   @ColumnInfo(name="paged_back") var pagedBack: Int = 0,
                   @ColumnInfo(name="search_back") var searchBack: Int = 0,
                   @ColumnInfo(name="paged_labels") var pagedLabel: Int = 0,
                   @ColumnInfo(name="search_glass") var searchGlass: Int = 0,
                   @ColumnInfo(name="search_mic") var searchMic: Int = 0,
                   @ColumnInfo(name="paged_cards") var pagedCards: Int = 0,
                   @ColumnInfo(name="apps_inner") var appsInner: Int = 0,
                   @ColumnInfo(name="apps_outer") var appsOuter: Int = 0,
                   @ColumnInfo(name="folder_icon") var folderIcon: Boolean = false,
                   @ColumnInfo(name="theme_name") var themeName: String = ""){
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate=true) var id: Long = 0
}





                   )