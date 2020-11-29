package pt.tetrapi.fgf.agroazores.models

import pt.tetrapi.fgf.agroazores.enums.UserRoleEnum

/**
 * @Author Fernando Nunes
 * @Email fernandonunes@tetrapi.pt
 * @Company Tetrapi
 *
 */

data class UserRole(
    val id: Int,
    val name: String
) {

    fun getRoleType(): UserRoleEnum {
        return when(id) {
            1 -> UserRoleEnum.Producer
            2 -> UserRoleEnum.Retailer
            else -> UserRoleEnum.EndUser
        }
    }



}