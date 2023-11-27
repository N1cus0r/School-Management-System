import {Grade} from "../../entities/Grade.ts";
import CardsContainer from "../shared/CardsContainer.tsx";
import React from "react";
import GradeCard from "./GradeCard.tsx";

interface Props {
  grades: Grade[];
}

const GradesCards = ({ grades }: Props) => {
  return (
    <CardsContainer>
      {grades.map((grade) => (
        <React.Fragment key={grade.id}>
          <GradeCard grade={grade} />
        </React.Fragment>
      ))}
    </CardsContainer>
  );
};
export default GradesCards;
