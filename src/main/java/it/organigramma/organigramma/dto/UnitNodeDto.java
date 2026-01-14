package it.organigramma.organigramma.dto;

import java.util.List;

public class UnitNodeDto {
    public String id;
    public String name;
    public List<UnitNodeDto> children;

    public UnitNodeDto(String id, String name, List<UnitNodeDto> children) {
        this.id = id;
        this.name = name;
        this.children = children;
    }
}
