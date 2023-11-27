import { Role } from "../entities/user/Role.ts";
import { Flex, HStack, Spinner } from "@chakra-ui/react";
import useTeachers from "../hooks/user/useTeachers.ts";
import useStudents from "../hooks/user/useStudents.ts";
import UsersCards from "../components/user/UsersCards.tsx";
import UsersSkeletons from "../components/user/UsersSkeletons.tsx";
import ContentPlaceholder from "../components/shared/ContentPlaceholder.tsx";
import SearchUserInput from "../components/user/SearchUserInput.tsx";
import CreateUserButton from "../components/user/CreateUserButton.tsx";
import useUsersSearch from "../hooks/user/useUsersSearch.ts";
import ErrorMessage from "../components/shared/ErrorMessage.tsx";
import React from "react";
import InfiniteScroll from "react-infinite-scroll-component";
import PageUtils from "../utils/page-utils.ts";

interface Props {
  usersRole: Role.TEACHER | Role.STUDENT;
}
const UsersPage = ({ usersRole }: Props) => {
  const handleSearch = useUsersSearch(usersRole);

  const {
    data: users,
    isLoading,
    error,
    fetchNextPage,
    hasNextPage,
  } = usersRole === Role.TEACHER ? useTeachers() : useStudents();

  if (isLoading) return <UsersSkeletons />;

  if (!users || error) return <ErrorMessage />;

  const fetchedDataCount = PageUtils.getFetchedDataCount(users);

  return (
    <>
      <Flex justifyContent="center">
        <HStack width="500px" spacing={3}>
          <CreateUserButton userRole={usersRole} />
          <SearchUserInput handleSearch={handleSearch} />
        </HStack>
      </Flex>
      {users.pages[0].length === 0 ? (
        <ContentPlaceholder />
      ) : (
        <InfiniteScroll
          next={() => fetchNextPage()}
          hasMore={!!hasNextPage}
          loader={<Spinner />}
          dataLength={fetchedDataCount}
        >
          {users.pages.map((page, index) => (
            <React.Fragment key={index}>
              <UsersCards users={page} />
            </React.Fragment>
          ))}
        </InfiniteScroll>
      )}
    </>
  );
};
export default UsersPage;
