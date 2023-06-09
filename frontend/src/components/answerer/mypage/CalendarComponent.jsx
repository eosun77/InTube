import React, { useState } from "react";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import moment from "moment";
import "./CalendarComponent.css";
import MatchList from "./MatchList.jsx";
import Typography from "@mui/material/Typography";

export default function CalendarComponent(props) {
  const [value, onChange] = useState(new Date());
  const interviewList = props.calendarInfo;

  return (
    <div>
      <Typography variant="h4" sx={{ mb: 2 }}>
        인터뷰 일정
      </Typography>
      <Calendar
        id="calendar"
        onChange={onChange}
        value={value}
        formatDay={(locale, date) => moment(date).format("DD")}
        navigationLabel={null}
        showNeighboringMonth={false}
        tileClassName={({ date, view }) => {
          if (
            interviewList.find(
              (x) =>
                x.interviewTimeRes.interview_start_time.slice(0, 10) ===
                moment(date).format("YYYY-MM-DD")
            )
          ) {
            return "highlight";
          }
        }}
      />
      <div>
        <Typography variant="h6" sx={{ my: 2 }}>
          {moment(value).format("YYYY년 MM월 DD일")}
        </Typography>

        <MatchList
          position={props.position}
          matchInfo={interviewList.filter(
            (x) =>
              x.interviewTimeRes.interview_start_time.slice(0, 10) ===
              moment(value).format("YYYY-MM-DD")
          )}
        />
      </div>
    </div>
  );
}
