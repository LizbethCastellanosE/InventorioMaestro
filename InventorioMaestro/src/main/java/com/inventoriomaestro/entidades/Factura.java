package com.inventoriomaestro.entidades;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "facturas")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")
    private long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_factura")
    private Date fecha;

    @Column(name = "tipo_factura")
    private String tipoDeFactura;

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL)
    private List<DetalleFactura> lineas = new ArrayList<>();


    public Factura() {
    }


    public Factura(Date fecha, String tipoDeFactura, Proveedor proveedor) {
        this.fecha = fecha;
        this.tipoDeFactura = tipoDeFactura;
        this.proveedor = proveedor;
    }

    // Getters y Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getTipoDeFactura() {
        return tipoDeFactura;
    }

    public void setTipoDeFactura(String tipoDeFactura) {
        this.tipoDeFactura = tipoDeFactura;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public List<DetalleFactura> getLineas() {
        return lineas;
    }

    public void setLineas(List<DetalleFactura> lineas) {
        this.lineas = lineas;
    }

    @Override
    public String toString() {
        return "Factura{" +
                "id=" + id +
                ", fecha=" + fecha +
                ", tipoDeFactura='" + tipoDeFactura + '\'' +
                ", proveedor=" + proveedor +
                ", lineas=" + lineas +
                '}';
    }
}
