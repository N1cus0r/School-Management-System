import useCourses from "../hooks/course/useCourses.ts";
import CoursesCards from "../components/course/CoursesCards.tsx";
import ContentPlaceholder from "../components/shared/ContentPlaceholder.tsx";
import { Flex, HStack, Spinner } from "@chakra-ui/react";
import SearchUserInput from "../components/user/SearchUserInput.tsx";
import CreateCourseButton from "../components/course/CreateCourseButton.tsx";
import useCoursesSearch from "../hooks/course/useCoursesSearch.ts";
import SkeletonCards from "../components/shared/SkeletonCards.tsx";
import ErrorMessage from "../components/shared/ErrorMessage.tsx";
import React from "react";
import InfiniteScroll from "react-infinite-scroll-component";

const CoursesPage = () => {
  const handleSearch = useCoursesSearch();

  const {
    data: courses,
    isLoading,
    error,
    fetchNextPage,
    hasNextPage,
  } = useCourses();

  if (isLoading) return <SkeletonCards />;

  if (!courses || error) return <ErrorMessage />;

  const fetchedDataCount = courses.pages.reduce(
    (total, page) => total + page.length,
    0,
  );

  return (
    <>
      <Flex justifyContent="center">
        <HStack width="500px" spacing={3}>
          <CreateCourseButton />
          <SearchUserInput handleSearch={handleSearch} />
        </HStack>
      </Flex>
      {courses.pages[0].length === 0 ? (
        <ContentPlaceholder />
      ) : (
        <InfiniteScroll
          next={() => fetchNextPage()}
          hasMore={!!hasNextPage}
          loader={<Spinner />}
          dataLength={fetchedDataCount}
        >
          {courses.pages.map((page, index) => (
            <React.Fragment key={index}>
              <CoursesCards courses={page} />
            </React.Fragment>
          ))}
        </InfiniteScroll>
      )}
    </>
  );
};
export default CoursesPage;
