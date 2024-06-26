package com.unseewr1.leafyreader.urifetcher

import android.content.Context
import android.os.Environment
import com.unseewr1.leafyreader.fileuri.TypedUri
import java.io.File


fun getSupportedFileUris(context: Context): List<TypedUri> {
    return getSupportedFileUrisDefault(context)
}


private fun getSupportedFileUrisDefault(context: Context): List<TypedUri> {
    val allVolumePdfFiles = mutableListOf<TypedUri>()
    val externalStorage = Environment.getExternalStorageDirectory()
    findSupportedFiles(context, externalStorage, allVolumePdfFiles)
    return allVolumePdfFiles.toList()
}

private fun findSupportedFiles(context: Context, directory: File, uris: MutableList<TypedUri>) {
    val files = directory.listFiles() ?: return
    for (file in files) {
        if (file.isDirectory) {
            findSupportedFiles(context, file, uris)
            continue
        }
        TypedUri.fromFile(context, file)?.let {
            uris.add(it)
        }
    }
}

