package com.example.orcsndwizards.objects;

import com.example.orcsndwizards.R;

public class Card {
    private int idPic;
    private String name;
    private String value;

    public Card(String name, String value) {
        this.name = name;
        if(this.name.equals("")) this.value = "";
        else this.value = value;
        selectedPic();
    }

    private void selectedPic() {
        switch(this.name){
            case "Orc" : this.idPic = R.drawable.orc;
                break;
            case "Wizard" : this.idPic = R.drawable.wizard;
                break;
            case "" : this.idPic = R.drawable.card;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
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
