import {User} from "../../entities/user/User.ts";
import React from "react";
import UserCard from "./UserCard.tsx";
import CardsContainer from "../shared/CardsContainer.tsx";

interface Props {
  users: User[];
}

const UsersCards = ({ users }: Props) => {
  return (
    <>
      <CardsContainer>
        {users.map((user) => (
          <React.Fragment key={user.id}>
            <UserCard user={user} />
          </React.Fragment>
        ))}
      </CardsContainer>
    </>
  );
};
export default UsersCards;
