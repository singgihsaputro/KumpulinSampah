package net.crevion.hackathon.kumpulinsampah.model;

public class SampahModel {

    private String sampah, thumbnailUrl;
    private String tanggal;
    private String idSampah;
    private String nama;

    public String getSampah() {
        return sampah;
    }

    public void setSampah(String name) {
        this.sampah = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String year) {
        this.tanggal = year;
    }

    public String getIdSampah() {
        return idSampah;
    }

    public void setIdSampah(String source) {
        this.idSampah = source;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String worth) {
        this.nama = worth;
    }
}
