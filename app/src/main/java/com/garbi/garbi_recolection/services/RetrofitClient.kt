package com.garbi.garbi_recolection.services
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context
import android.util.Log
import androidx.navigation.NavController

object RetrofitClient {
    private const val BASE_URL = "http://54.152.182.89"
    private const val PREFERENCES_NAME = "UserSession"
    private var token: String? = null
    private var tokenExpiryTime: Long? = null
    private const val TOKEN_EXPIRY_TIME_HOURS = 24

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("accept", "application/json")

                token?.let {
                    requestBuilder.header("Authorization", "Bearer $it")
                }

                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()
    }

    val containerService: ContainerService by lazy {
        retrofit.create(ContainerService::class.java)
    }

    val loginService: LoginService by lazy {
        retrofit.create(LoginService::class.java)
    }

    val reportService: ReportService by lazy {
        retrofit.create(ReportService::class.java)
    }

    fun setToken(context: Context, tokenSet: String){
        val expiryTime = System.currentTimeMillis() + (TOKEN_EXPIRY_TIME_HOURS * 3600000)
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        token = tokenSet
        tokenExpiryTime = expiryTime
        editor.putString("token", tokenSet)
        editor.putLong("tokenExpiryTime", expiryTime)
        editor.apply()
        Log.v("session","Token seteado ${token}")
    }

    fun getToken(context: Context): String? {
        if(token == null){
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            token = sharedPreferences.getString("token", null)
            tokenExpiryTime = sharedPreferences.getLong("tokenExpiryTime", 0L)
            Log.v("session","Token encontrado en sharedpreferences ${token}")
        }else{
            Log.v("session","Token ya estaba ${token}")
        }
        Log.v("session", "El token expira en ${tokenExpiryTime} y es ${System.currentTimeMillis()}")
        return token
    }
    fun deleteToken(context: Context) {
        token = null
        tokenExpiryTime = null
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("token")
        editor.remove("tokenExpiryTime")
        editor.apply()
    }

    fun isTokenValid(): Boolean {
        return System.currentTimeMillis() < tokenExpiryTime!!
    }
    suspend fun getSession(context: Context, navController: NavController): UserDetails? {
        val session = getStoredSession(context)
        if (session == null){
            deleteSession(context)
            deleteToken(context)
            navController.navigate("login")
        }
        return session
    }


    suspend fun setSession(context: Context, password: String): UserDetails? {
        Log.v("session","setSession")
        var session: UserDetails? = null
        val loginResponse = loginService.session(SessionRequest(token ?: ""))
        if (loginResponse.success) {
            session = loginResponse.user
            storeSession(context, session, password)
        }
        Log.v("session",session.toString() + " pwd " + password)
        return session
    }

    fun getStoredSession(context: Context): UserDetails? {
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)
        val companyId = sharedPreferences.getString("companyId", null)
        val name = sharedPreferences.getString("name", null)
        val surname = sharedPreferences.getString("surname", null)
        val phone = sharedPreferences.getString("phone", null)
        val email = sharedPreferences.getString("email", null)
        val role = sharedPreferences.getString("role", null)
        val password = sharedPreferences.getString("password", null)

        return if (userId != null && name != null && email != null) {
            UserDetails(userId, companyId ?: "", name, surname ?: "", phone ?: "", email, role ?: "", password ?: "")
        } else {
            null
        }
    }

    private fun storeSession(context: Context, userDetails: UserDetails, password: String) {
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userId", userDetails._id)
        editor.putString("companyId", userDetails.companyId)
        editor.putString("name", userDetails.name)
        editor.putString("surname", userDetails.surname)
        editor.putString("phone", userDetails.phone)
        editor.putString("email", userDetails.email)
        editor.putString("role", userDetails.role)
        editor.putString("password", password)
        editor.apply()
    }

    fun deleteSession(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("userId")
        editor.remove("companyId")
        editor.remove("name")
        editor.remove("surname")
        editor.remove("phone")
        editor.remove("email")
        editor.remove("role")
        editor.remove("password")
        editor.apply()
    }

}