package com.example.gamevault.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gamevault.ui.theme.Bege
import com.example.gamevault.ui.theme.Branco
import com.example.gamevault.ui.theme.Laranja
import com.example.gamevault.ui.theme.Marrom
import com.example.gamevault.ui.theme.Preto

private val itensDoMenu = listOf(
    "Biblioteca" to Icons.Default.Home,
    "Buscar" to Icons.Default.Search,
    "Lançamentos" to Icons.Default.DateRange,
    "Perfil" to Icons.Default.Person
)

@Composable
fun MenuLateral(
    drawerState: DrawerState,
    itemSelecionado: String,
    onItemClick: (String) -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = Preto) {
                Text(
                    text = "GameVault",
                    color = Laranja,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(24.dp)
                )

                itensDoMenu.forEach { (nome, icone) ->
                    NavigationDrawerItem(
                        label = { Text(nome) },
                        icon = { Icon(icone, contentDescription = nome) },
                        selected = nome == itemSelecionado,
                        onClick = { onItemClick(nome) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Marrom,
                            unselectedContainerColor = Color.Transparent,
                            selectedIconColor = Branco,
                            unselectedIconColor = Bege,
                            selectedTextColor = Branco,
                            unselectedTextColor = Bege
                        )
                    )
                }
            }
        },
        content = content
    )
}