/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */package au.edu.jcu.cp5307.proj2.application.kenken.core.operator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class Operator {
    protected abstract BigDecimal reduce(List<BigDecimal> operands);
    public abstract boolean isCommutative();
    public abstract String notation();

    public final BigDecimal apply(List<Integer> operands) {
        return reduce(operands.stream().map(BigDecimal::valueOf).collect(Collectors.toList()));
    }

    public boolean hasCommutativeInverse() {
        return false;
    }
    
    @NonNull
    @Override
    public String toString() {
        return notation();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        else if (!(obj instanceof Operator)) {
            return false;
        }

        Operator other = (Operator) obj;
        return notation().equals(other.notation());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(notation());
        return hash;
    }
}
