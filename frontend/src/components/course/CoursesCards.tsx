import React from "react";
import {Course} from "../../entities/Course.ts";
import CourseCard from "./CourseCard.tsx";
import CardsContainer from "../shared/CardsContainer.tsx";

interface Props {
  courses: Course[];
}

const CoursesCards = ({ courses }: Props) => {
  return (
    <CardsContainer>
      {courses.map((course) => (
        <React.Fragment key={course.id}>
          <CourseCard course={course} />
        </React.Fragment>
      ))}
    </CardsContainer>
  );
};
export default CoursesCards;
