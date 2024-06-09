package com.garbi.garbi_recolection.services
import android.content.SharedPreferences
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context
import android.util.Log

object RetrofitClient {
    private const val BASE_URL = "http://54.152.182.89"
    private const val PREFERENCES_NAME = "UserSession"
    private var token: String? = null

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

    fun setToken(token: String){
        this.token = token;
    }

    suspend fun getSession(context: Context): UserDetails? {
        var session = getStoredSession(context)
        if (session == null) {
            Log.v("session","No estaba guardada la session. Buscandola...")
            val loginResponse = loginService.session(SessionRequest(token ?: ""))
            if (loginResponse.success) {
                session = loginResponse.user.user
                storeSession(context, session)
            }
        }else{
            Log.v("session","Estaba guardada la session.")
        }
        Log.v("session",session.toString())
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

        return if (userId != null && name != null && email != null) {
            UserDetails(userId, companyId ?: "", name, surname ?: "", phone ?: "", email, role ?: "")
        } else {
            null
        }
    }

    private fun storeSession(context: Context, userDetails: UserDetails) {
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userId", userDetails._id)
        editor.putString("companyId", userDetails.companyId)
        editor.putString("name", userDetails.name)
        editor.putString("surname", userDetails.surname)
        editor.putString("phone", userDetails.phone)
        editor.putString("email", userDetails.email)
        editor.putString("role", userDetails.role)
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
        editor.apply()
    }
}