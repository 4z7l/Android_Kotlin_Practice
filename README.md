# 안드로이드 카카오 로그인 연동

### [1. 애플리케이션 등록](https://developers.kakao.com/docs/latest/ko/getting-started/app)

### [2. 플랫폼 등록](https://developers.kakao.com/docs/latest/ko/getting-started/app#platform)

> :bulb: [키 해시 등록](https://developers.kakao.com/docs/latest/ko/getting-started/sdk-android#add-key-hash) : 
> ```kotlin
> import com.kakao.sdk.common.util.Utility 
> var keyHash = Utility.getKeyHash(this)
> ```

### [3. 카카오 로그인 활성화](https://developers.kakao.com/docs/latest/ko/getting-started/app#kakao-login)

### [4. 동의항목 설정](https://developers.kakao.com/docs/latest/ko/getting-started/app#agreement)

### [5. Redirect URI 설정](https://developers.kakao.com/docs/latest/ko/getting-started/app#redirect-uri)

> :bulb: 그냥 임의의 주소로 설정하면 된다.

### [6. 개발환경 설정](https://developers.kakao.com/docs/latest/ko/getting-started/sdk-android)

- Gradle 설정 : Project 레벨, module 레벨
- Manifest 설정 : 인터넷 사용권한 설정
- JAVA 8 사용 설정

### [7. 카카오 SDK 초기화](https://developers.kakao.com/docs/latest/ko/getting-started/sdk-android#init)

- strings.xml

```xml
<string name="kakao_app_key">1b68581a7f477df2eaa47fa7c2a1789c</string>
```

- MyApplication.kt

```kotlin
class MyApplication : Application() {  
    override fun onCreate() {  
        super.onCreate()  
  
		// Kakao SDK 초기화  
		KakaoSdk.init(this, getString(R.string.kakao_app_key))  
    }  
}
```

### [8. 초기화](https://developers.kakao.com/docs/latest/ko/kakaologin/android#before-you-begin)

- AuthCodeHandlerActivity 설정

```xml
<activity android:name="com.kakao.sdk.auth.AuthCodeHandlerActivity">  
    <intent-filter>  
        <action android:name="android.intent.action.VIEW" />  
  
        <category android:name="android.intent.category.DEFAULT" />  
        <category android:name="android.intent.category.BROWSABLE" />  
  
        <!-- Redirect URI: "kakao{NATIVE_APP_KEY}://oauth“ -->  
  <data  
  android:host="oauth"  
  android:scheme="kakao{NATIVE_APP_KEY}" />  
    </intent-filter>  
</activity>
```

> :bulb: `kakao{NATIVE_APP_KEY}`는 네이티브 앱 키가 12345인경우 `kakao12345`로 설정

- `android:name` 설정
```xml
<application  
  android:name=".MyApplication"
  ...>
</application>
```

### [9. 로그인 로직 구현](https://developers.kakao.com/docs/latest/ko/kakaologin/android#sample-login)

```kotlin
class LoginActivity : AppCompatActivity() {  
    val TAG = "LoginActivity"  
  
  override fun onCreate(savedInstanceState: Bundle?) {  
        super.onCreate(savedInstanceState)  
        setContentView(R.layout.activity_login)  
  
        // 로그인 공통 callback 구성  
  val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->  
  if (error != null) {  
                Log.e(TAG, "로그인 실패", error)  
            }  
            else if (token != null) {  
                Log.i(TAG, "로그인 성공 ${token.accessToken}")  
                startMainActivity()  
            }  
        }  
  
  kakao_login_btn.setOnClickListener{  
  // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인  
  if (LoginClient.instance.isKakaoTalkLoginAvailable(this)) {  
                LoginClient.instance.loginWithKakaoTalk(this, callback = callback)  
            } else {  
                LoginClient.instance.loginWithKakaoAccount(this, callback = callback)  
            }  
        }  
  }  
  
    fun startMainActivity() {  
        startActivity(Intent(this, MainActivity::class.java))  
    }  
}
```
