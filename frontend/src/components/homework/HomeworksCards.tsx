import {Homework} from "../../entities/Homework.ts";
import CardsContainer from "../shared/CardsContainer.tsx";
import React from "react";
import HomeworksCard from "./HomeworksCard.tsx";

interface Props {
    homeworks: Homework[]
}
const HomeworksCards = ({homeworks}: Props) => {
    return (
        <CardsContainer>
            {homeworks.map((homework) => (
                <React.Fragment key={homework.id}>
                    <HomeworksCard  homework={homework}/>
                </React.Fragment>
            ))}
        </CardsContainer>
    )
}
export default HomeworksCards
