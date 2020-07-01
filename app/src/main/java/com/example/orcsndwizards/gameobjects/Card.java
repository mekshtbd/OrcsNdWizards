package com.example.orcsndwizards.gameobjects;

import com.example.orcsndwizards.R;

public class Card {
    private int idPic;
    private String name;
    private String value;
    private boolean special;

    public Card(String name, String value, boolean special,int idPic ) {
        this.name = name;
        this.special = special;
        this.idPic = idPic;
        this.value = value;
    }

    public Card(String name, int idPic){
        this.value = "";
        this.special = false;
        this.name = name;
        this.idPic = idPic;
    }

    public Card() {
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

    public boolean isSpecial() {
        return special;
    }

    public void setSpecial(boolean special) {
        this.special = special;
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
