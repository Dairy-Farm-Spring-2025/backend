package com.capstone.dfms.models;

import com.capstone.dfms.models.compositeKeys.UserSchedulePK;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_schedules")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserScheduleEntity {
    @EmbeddedId
    private UserSchedulePK id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @MapsId("scheduleId")
    @JoinColumn(name = "schedule_id")
    private ScheduleEntity schedule;
}
