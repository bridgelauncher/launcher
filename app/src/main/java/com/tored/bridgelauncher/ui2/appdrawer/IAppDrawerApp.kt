package com.tored.bridgelauncher.ui2.appdrawer

interface IAppDrawerApp
{
    val packageName: String
    val label: String
}

data class TestApp(
    override val packageName: String,
    override val label: String,
): IAppDrawerApp