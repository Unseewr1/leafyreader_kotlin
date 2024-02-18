package com.unseewr1.leafyreader.urifetcher

import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File


fun getSupportedFileUris(context: Context): List<Uri> {
    return getSupportedFileUrisDefault(context)
}


private fun getSupportedFileUrisDefault(context: Context): List<Uri> {
    val allVolumePdfFiles = mutableListOf<Uri>()
    val externalStorage = Environment.getExternalStorageDirectory()
    findPdfFiles(externalStorage, allVolumePdfFiles)
    return allVolumePdfFiles.toList()
}

private fun findPdfFiles(directory: File, pdfFiles: MutableList<Uri>) {
    val files = directory.listFiles() ?: return
    for (file in files) {
        if (file.isDirectory) {
            findPdfFiles(file, pdfFiles)
        } else if (file.extension.equals("pdf", ignoreCase = true)) {
            pdfFiles.add(Uri.fromFile(file))
        }
    }
}