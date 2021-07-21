package com.lonelybytes.swiftlint

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

class SwiftLintConfig {
//    class Config(aPath: String) {
//        var file: File = File(aPath)
//
//        private var _lastUpdateTime: Long = 0
//
//        private fun updateIfNeeded() {
//            if (file.lastModified() > _lastUpdateTime) {
//                _lastUpdateTime = file.lastModified()
//            }
//        }
//
//        init {
//            updateIfNeeded()
//        }
//    }

    private class DepthedFile(var _depth: Int, var _file: VirtualFile)

    companion object {
        const val FILE_NAME = ".swiftlint.yml"

        private fun configExistsInDir(aPath: String): Boolean {
            return File("$aPath/$FILE_NAME").exists()
        }

        private fun configPathInBetween(rootPath: String, upToPath: String): String? {
            var testPath = upToPath.substringBeforeLast("/")
            while (testPath.contains(rootPath) && !configExistsInDir(testPath)) {
                testPath = testPath.substringBeforeLast("/")
            }

            return if (configExistsInDir(testPath)) {
                testPath
            } else {
                null
            }
        }

        fun swiftLintConfigPath(aProject: Project, aFileToFindConfigFor: VirtualFile): String? {
            val projectBasePath = aProject.basePath
            val filePath = aFileToFindConfigFor.path
            return if (projectBasePath != null && configExistsInDir(projectBasePath) && filePath.contains(projectBasePath)) {
                projectBasePath
            } else {
                ProjectRootManager.getInstance(aProject)
                        .contentSourceRoots
                        .filter { it.isDirectory && filePath.contains(it.path) }
                        .mapNotNull { configPathInBetween(it.path, filePath) }
                        .firstOrNull()
            }
        }
    }
}