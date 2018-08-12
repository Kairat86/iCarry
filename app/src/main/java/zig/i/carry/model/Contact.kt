package zig.i.carry.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.io.Serializable

@Entity
class Contact(var value: String = "") : Serializable {

    @Id
    var id: Long? = null

    override fun toString(): String {
        return "Contact(value='$value', id=$id)"
    }


}