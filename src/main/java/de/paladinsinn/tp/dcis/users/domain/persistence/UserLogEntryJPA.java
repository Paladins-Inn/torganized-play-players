package de.paladinsinn.tp.dcis.users.domain.persistence;

import java.util.UUID;

import de.kaiserpfalzedv.commons.jpa.AbstractJPAEntity;
import de.paladinsinn.tp.dcis.users.domain.model.UserLogEntry;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * The player action log.
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0
 */
@Entity
@Table(
    name = "PLAYERLOG",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ID"}),
    }
)
@Jacksonized
@SuperBuilder(toBuilder = true, setterPrefix = "")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Data
@ToString(callSuper = true, includeFieldNames = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true)
public class UserLogEntryJPA extends AbstractJPAEntity<UUID> implements UserLogEntry {
    @NotNull
    @ManyToOne
    @JoinColumn(name = "PLAYER", columnDefinition = "UUID", unique = false, nullable = false, insertable = true, updatable = false)
    @ToString.Include
    private UserJPA user;

    @NotNull
    @Column(name = "SYSTEM", columnDefinition = "VARCHAR(100)", unique = false, nullable = false, insertable = true, updatable = true)
    @Size(min = 3, max = 100, message = "The length of the string must be between 3 and 100 characters long.") 
    @Pattern(regexp = "^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$", message = "The string must match the pattern '^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$'")
    private String system;

    @NotNull
    @Column(name = "ENTRY", columnDefinition = "VARCHAR(1000)", unique = false, nullable = false, insertable = true, updatable = true)
    @Size(min = 3, max = 1000, message = "The length of the string must be between 3 and 100 characters long.") 
    private String text;

    /**
     * Will prevent the updating of the data set.
     */
    @PreUpdate
    public void thisDataIsNotUpdatable() {
        throw new UnsupportedOperationException("This data set is immutable.");
    }
}
