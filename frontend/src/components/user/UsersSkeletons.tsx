import React from "react";
import UserSkeleton from "./UserSkeleton.tsx";
import CardsContainer from "../shared/CardsContainer.tsx";

const UsersSkeletons = () => {
  const skeletons = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15];

  return (
    <>
      <CardsContainer>
        {skeletons.map((skeleton) => (
          <React.Fragment key={skeleton}>
            <UserSkeleton />
          </React.Fragment>
        ))}
      </CardsContainer>
    </>
  );
};
export default UsersSkeletons;
