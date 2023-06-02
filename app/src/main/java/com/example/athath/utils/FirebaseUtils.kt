package com.example.athath.utils

import com.example.athath.data.models.CartProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

class FirebaseUtils(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {


    private val cartCollection =
        firestore.collection("user").document(auth.uid!!).collection("cart")


    fun addToCart(cartProduct: CartProduct, onResult: (CartProduct?, Exception?) -> Unit) {
        // auto generating document id
        cartCollection.document().set(cartProduct)
            .addOnSuccessListener {
                onResult(cartProduct, null)
            }
            .addOnFailureListener {
                onResult(null, it)
            }
    }

    fun increaseProductQuantity(
        documentId: String,
        onResult: (String?, Exception?) -> Unit
    ) {
        firestore.runTransaction { transaction ->
            val documentRef = cartCollection.document(documentId)
            val document = transaction.get(documentRef)
            val documentObj = document.toObject(CartProduct::class.java)
            documentObj?.let { cartProduct ->
                val newQuantity = cartProduct.quantity + 1
                val newProduct = cartProduct.copy(quantity = newQuantity)
                transaction.set(documentRef, newProduct)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }



    fun decreaseProductQuantity(
        documentId: String,
        onResult: (String?, Exception?) -> Unit,
    ) {
        firestore.runTransaction { transaction ->
            val documentRef = cartCollection.document(documentId)
            val document = transaction.get(documentRef)
            val documentObj = document.toObject(CartProduct::class.java)
            documentObj?.let { cartProduct ->
                val newQuantity = cartProduct.quantity - 1
                val newProduct = cartProduct.copy(quantity = newQuantity)
                transaction.set(documentRef, newProduct)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }


    enum class IncreaseDecreaseProduct {
        INCREASE, DECREASE
    }






}