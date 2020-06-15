package com.example.orcsndwizards;

public class Card {
    private int idPic;
    private String name;
    private int value;

    public Card(String name, int value) {
        this.name = name;
        this.value = value;
        selectedPic();
    }

    private void selectedPic() {
        switch(this.name){
            case "Orc" : this.idPic = R.drawable.orc;
                break;
            case "Wizard" : this.idPic = R.drawable.wizard;
                break;
        }
    }

    public int getIdPic() {
        return idPic;
    }

    public void setIdPic(int idPic) {
        this.idPic = idPic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Card{" +
                "idPic=" + idPic +
                ", name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
