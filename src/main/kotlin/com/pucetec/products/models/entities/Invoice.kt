package com.pucetec.products.models.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Transient

@Entity
@Table(name = "invoices")
data class Invoice (
    @Column(name = "client_ci")
    val clientCi: String,

    @Column(name = "client_name")
    val clientName: String,

    @Column(name = "client_address")
    val clientAddress: String,

    @Column(name = "total_before_taxes")
    val totalBeforeTaxes: Float? = null,

    val taxes: Float? = null,

    @Column(name = "total_after_taxes")
    val totalAfterTaxes: Float? = null,

    // 🔹 Ya NO es @OneToMany, ahora es solo un campo de apoyo
    @Transient
    var invoiceDetails: MutableList<InvoiceDetail> = mutableListOf(),

    ) : BaseEntity()
