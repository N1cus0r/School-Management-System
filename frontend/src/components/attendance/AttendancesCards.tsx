import {Attendance} from "../../entities/attendance/Attendance.ts";
import React from "react";
import CardsContainer from "../shared/CardsContainer.tsx";
import AttendanceCard from "./AttendanceCard.tsx";

interface Props {
    attendances: Attendance[]
}

const AttendancesCards = ({attendances}: Props) => {
    return (
        <CardsContainer>
            {attendances.map((attendance) => (
                <React.Fragment key={attendance.id}>
                    <AttendanceCard attendance={attendance}/>
                </React.Fragment>
            ))}
        </CardsContainer>
    )
}
export default AttendancesCards
