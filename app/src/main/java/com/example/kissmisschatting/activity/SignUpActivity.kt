package com.example.kissmisschatting.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kissmisschatting.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_sign_up)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnSignUp.setOnClickListener {
            val userName = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            var strGender: String? = null

            if (binding.radioGroup.getCheckedRadioButtonId() == -1) {
                strGender = null
                Toast.makeText(applicationContext,"Gender is required", Toast.LENGTH_SHORT).show()
            } else {
                if (binding.male.isChecked()) {     // one of the radio buttons is checked
                    strGender = "Male"
                } else if (binding.female.isChecked()) {
                    strGender = "Female"
                } else {
                    strGender = "3"
                }
            }


            if (TextUtils.isEmpty(userName)){
                Toast.makeText(applicationContext,"username is required", Toast.LENGTH_SHORT).show()
            }
            else if (TextUtils.isEmpty(email)){
                Toast.makeText(applicationContext,"email is required", Toast.LENGTH_SHORT).show()
            }

            else if (TextUtils.isEmpty(password)){
                Toast.makeText(applicationContext,"password is required", Toast.LENGTH_SHORT).show()
            }

            else if (TextUtils.isEmpty(confirmPassword)){
                Toast.makeText(applicationContext,"confirm password is required", Toast.LENGTH_SHORT).show()
            }

            else if (!password.equals(confirmPassword)){
                Toast.makeText(applicationContext,"password not match", Toast.LENGTH_SHORT).show()
            }
            else {
                if (binding.radioGroup.getCheckedRadioButtonId() == -1) {
                    strGender = null
                    Toast.makeText(applicationContext,"Gender is required", Toast.LENGTH_SHORT).show()
                }
                else{
                    registerUser(userName, email, password,strGender!!)
                    intent.putExtra("currentUserName", userName)
                }
            }

        }

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this@SignUpActivity,
                LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser(userName:String,email:String,password:String,strGender:String){
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    val user: FirebaseUser? = auth.currentUser
                    val userId:String = user!!.uid

                    databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)

                    val hashMap:HashMap<String,String> = HashMap()
                    hashMap.put("userId",userId)
                    hashMap.put("userName",userName)
                    hashMap.put("profileImage","")
                    hashMap.put("gender",strGender)

                    databaseReference.setValue(hashMap).addOnCompleteListener(this){
                        if (it.isSuccessful){
                            //open home activity
                            binding.etName.setText("")
                            binding.etEmail.setText("")
                            binding.etPassword.setText("")
                            binding.etConfirmPassword.setText("")
                            val intent = Intent(this@SignUpActivity,
                                UsersActivity::class.java)
                            startActivity(intent)
                            finish()
                           /* Toast.makeText(
                                applicationContext,
                                "Registration Done",
                                Toast.LENGTH_SHORT
                            ).show()*/
                        }
                    }
                }
            }
            .addOnFailureListener(this){
                println(it.message)
                Toast.makeText(
                    applicationContext,
                    it.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}

