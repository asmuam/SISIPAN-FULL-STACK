object UserRepository {

    private var accessToken: String? = null
    private var role: String? = null
    private var id: Long? = null
    private var email: String? = null
    private var idMhs: Long? = null

    fun setAccessToken(token: String) {
        accessToken = token
    }

    fun getAccessToken(): String? {
        return accessToken
    }

    fun setRole(userRole: String) {
        role = userRole
    }

    fun getRole(): String? {
        return role
    }

    fun setId(userId: Long) {
        id = userId
    }

    fun getId(): Long? {
        return id
    }

    fun setEmail(userEmail: String) {
        email = userEmail
    }

    fun getEmail(): String? {
        return email
    }

    fun setIdMhs(studentId: Long) {
        idMhs = studentId
    }

    fun getIdMhs(): Long? {
        return idMhs
    }

    fun setAllUserData(
        accessToken: String,
        role: String,
        id: Long,
        email: String,
        idMhs: Long
    ) {
        setAccessToken(accessToken)
        setRole(role)
        setId(id)
        setEmail(email)
        setIdMhs(idMhs)
    }
}
