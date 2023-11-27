import { useParams } from "react-router-dom";
import SkeletonCards from "../components/shared/SkeletonCards.tsx";
import ErrorMessage from "../components/shared/ErrorMessage.tsx";
import ContentPlaceholder from "../components/shared/ContentPlaceholder.tsx";
import useCourseAttendances from "../hooks/attendance/useCourseAttendances.ts";
import AttendancesCards from "../components/attendance/AttendancesCards.tsx";
import { Flex, Spinner } from "@chakra-ui/react";
import CreateAttendanceButton from "../components/attendance/CreateAttendanceButton.tsx";
import React from "react";
import InfiniteScroll from "react-infinite-scroll-component";
import PageUtils from "../utils/page-utils.ts";

const CourseAttendancesPage = () => {
  const { courseId } = useParams();

  const {
    data: attendances,
    isLoading,
    error,
    fetchNextPage,
    hasNextPage,
  } = useCourseAttendances(courseId!);

  if (isLoading) return <SkeletonCards />;

  if (!attendances || error) return <ErrorMessage />;

  const fetchedDataCount = PageUtils.getFetchedDataCount(attendances);

  return (
    <>
      <Flex justifyContent="center">
        <CreateAttendanceButton />
      </Flex>
      {attendances.pages[0].length === 0 ? (
        <ContentPlaceholder />
      ) : (
        <InfiniteScroll
          next={() => fetchNextPage()}
          hasMore={!!hasNextPage}
          loader={<Spinner />}
          dataLength={fetchedDataCount}
        >
          {attendances.pages.map((page, index) => (
            <React.Fragment key={index}>
              <AttendancesCards attendances={page} />
            </React.Fragment>
          ))}
        </InfiniteScroll>
      )}
    </>
  );
};
export default CourseAttendancesPage;
