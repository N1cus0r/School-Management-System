import React from "react";
import CardsContainer from "./CardsContainer.tsx";
import SkeletonCard from "./SkeletonCard.tsx";

const SkeletonCards = () => {
  const skeletons = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15];
  return (
    <CardsContainer>
      {skeletons.map((skeleton) => (
        <React.Fragment key={skeleton}>
          <SkeletonCard/>
        </React.Fragment>
      ))}
    </CardsContainer>
  );
};
export default SkeletonCards;
